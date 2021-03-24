package com.example.androidremake2.views.podcast;

import android.app.Dialog;
import android.content.DialogInterface;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.example.androidremake2.R;
import com.example.androidremake2.core.podcast.Podcast;
import com.example.androidremake2.core.podcast.PodcastEpisode;
import com.example.androidremake2.databinding.DlgModalBottomSheetBinding;
import com.example.androidremake2.utils.Logs;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.slider.Slider;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class PodcastBottomSheetDialogFragment extends BottomSheetDialogFragment implements Runnable {

    protected DlgModalBottomSheetBinding binding;

    protected MediaPlayer mediaPlayer;

    protected Thread audioSliderThread;

    protected View.OnClickListener onPlayPauseClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (mediaPlayer.isPlaying()) {
                binding.btnPlayPause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play, null));
                if (audioSliderThread != null) {
                    audioSliderThread.interrupt();
                    audioSliderThread = null;
                }
                mediaPlayer.pause();
            } else {
                binding.btnPlayPause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause, null));

                mediaPlayer.start();

                if (audioSliderThread == null) {
                    audioSliderThread = new Thread(PodcastBottomSheetDialogFragment.this);
                    audioSliderThread.start();
                }
            }
        }
    };

    protected Slider.OnSliderTouchListener onSliderTouch = new Slider.OnSliderTouchListener() {
        @Override
        public void onStartTrackingTouch(@NonNull Slider slider) {
            int msec = ((int) slider.getValue()) * 1000;
            mediaPlayer.seekTo(msec);
        }

        @Override
        public void onStopTrackingTouch(@NonNull Slider slider) {
            int msec = ((int) slider.getValue()) * 1000;
            mediaPlayer.seekTo(msec);
        }
    };

    public void writeDuration(int position, int duration) {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int secs = position % 60;
                int mins = position / 60;

                if (binding != null)
                    binding.startDurationTxt.setText(String.format("%02d:%02d", mins, secs));

                int remaining = duration - position;
                secs = remaining % 60;
                mins = remaining / 60;

                if (binding != null)
                    binding.endDurationTxt.setText(String.format("%02d:%02d", mins, secs));
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(binding.podcastBottomSheet);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                Logs.debug(this, "[DBG] state: " + bottomSheetBehavior.getState());
            }
        });
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DlgModalBottomSheetBinding.inflate(inflater, container, false);

        Podcast podcast = PodcastBottomSheetDialogFragmentArgs.fromBundle(getArguments()).getPodcast();

        displayPodcastEpisode(podcast);
        startPodcast(podcast);

        return binding.getRoot();
    }

    public void displayPodcastEpisode(Podcast podcast) {
        PodcastEpisode episode = podcast.episodes.get(0);

        binding.btnPlayPause.setOnClickListener(onPlayPauseClick);

        writeDuration(0, episode.duration);

        // Setup slider
        binding.podcastSlider.setValueFrom(0);
        binding.podcastSlider.setValueTo(episode.duration);
        binding.podcastSlider.addOnSliderTouchListener(onSliderTouch);

        binding.txtPodcastTitle.setText(episode.title);
        binding.txtPodcastAuthor.setText(podcast.publisher);

        Picasso.get().load(episode.imageUrl).into(binding.podcastImg);
    }

    public void startPodcast(Podcast podcast) {
        PodcastEpisode firstEpisode = podcast.episodes.get(0);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );

        try {
            mediaPlayer.setDataSource(firstEpisode.audioUrl);
            mediaPlayer.prepare();
            mediaPlayer.start();
            audioSliderThread = new Thread(this);
            audioSliderThread.start();

        } catch (IOException e) {
            Logs.error(this, e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            int currentPos = mediaPlayer.getCurrentPosition() / 1000;
            int duration = mediaPlayer.getDuration() / 1000;

            while (mediaPlayer != null && mediaPlayer.isPlaying() && currentPos < duration) {
                try {
                    Thread.sleep(1000);
                    if (mediaPlayer == null)
                        break;
                    currentPos = mediaPlayer.getCurrentPosition() / 1000;
                } catch (InterruptedException e) {
                    Logs.error(this, e.getMessage());
                }
                binding.podcastSlider.setValue(currentPos);
                writeDuration(currentPos, duration);
            }

            if (binding != null)
                binding.btnPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play, null));
        } catch (IllegalStateException e) {
            Logs.error(this, e.getMessage());
        }
    }

    @Override
    public void onDestroy() {

        if (audioSliderThread != null)
            audioSliderThread.interrupt();

        audioSliderThread = null;

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        mediaPlayer = null;
        
        binding.btnPlayPause.setOnClickListener(null);

        binding.podcastSlider.removeOnSliderTouchListener(onSliderTouch);
        
        binding = null;
        super.onDestroy();
    }
}
