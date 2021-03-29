package com.example.androidremake2.views.podcast;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

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

    protected Podcast podcast;

    protected View.OnClickListener onPlayPauseClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            playPausePodcast();
        }
    };

    protected View.OnClickListener onSkipNextClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            skipNextPodcast();
        }
    };

    protected Slider.OnSliderTouchListener onSliderTouch = new Slider.OnSliderTouchListener() {
        @Override
        public void onStartTrackingTouch(@NonNull Slider slider) {
            /*
            writeDuration(slider.getValue(), slider.getValueTo());

            int msec = ((int) slider.getValue()) * 1000;
            mediaPlayer.seekTo(msec);
             */
        }

        @Override
        public void onStopTrackingTouch(@NonNull Slider slider) {

            writeDuration((int) slider.getValue(), (int) slider.getValueTo());

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
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog dialog = (BottomSheetDialog) dialogInterface;
                View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        return bottomSheetDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DlgModalBottomSheetBinding.inflate(inflater, container, false);

        binding.podcastSlider.addOnSliderTouchListener(onSliderTouch);
        binding.btnPlayPause.setOnClickListener(onPlayPauseClick);
        binding.btnSkipNext.setOnClickListener(onSkipNextClick);

        binding.podcastSlider.setValueFrom(0);

        initMediaPlayer();

        this.podcast = PodcastBottomSheetDialogFragmentArgs.fromBundle(getArguments()).getPodcast();

        binding.txtPodcastAuthor.setText(podcast.publisher);

        PodcastEpisode episode = displayPodcastEpisode(this.podcast);
        startPodcastEpisode(episode);

        return binding.getRoot();
    }

    public void initMediaPlayer() {
        this.mediaPlayer = new MediaPlayer();
        this.mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
    }

    public PodcastEpisode displayPodcastEpisode(Podcast podcast) {
        PodcastEpisode episode = podcast.getNextEpisode();

        if (episode == null) {
            return null;
        }

        // Setup slider
        binding.podcastSlider.setValue(0);

        binding.txtPodcastTitle.setText(episode.title);
        Picasso.get().load(episode.imageUrl).into(binding.podcastImg);

        return episode;
    }

    public void startPodcastEpisode(PodcastEpisode episode) {
        try {
            mediaPlayer.setDataSource(episode.audioUrl);
            mediaPlayer.prepare();

            Drawable pauseDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_pause);
            binding.btnPlayPause.setImageDrawable(pauseDrawable);

            int durationSec = mediaPlayer.getDuration() / 1000;
            binding.podcastSlider.setValueTo(durationSec);
            writeDuration(0, durationSec);

            mediaPlayer.start();

            requireActivity().runOnUiThread(this);
            //audioSliderThread = new Thread(this);
            //audioSliderThread.start();

        } catch (IOException e) {
            Logs.error(this, e.getMessage());
        }
    }

    public void playPausePodcast() {

        if (mediaPlayer.isPlaying()) {
            pausePodcast();

            Drawable playDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_play);
            binding.btnPlayPause.setImageDrawable(playDrawable);
        } else {
            Drawable pauseDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_pause);
            binding.btnPlayPause.setImageDrawable(pauseDrawable);

            mediaPlayer.start();

            if (audioSliderThread == null) {
                audioSliderThread = new Thread(PodcastBottomSheetDialogFragment.this);
                audioSliderThread.start();
            }
        }
    }

    public void pausePodcast() {
        if (audioSliderThread != null) {
            audioSliderThread.interrupt();
            audioSliderThread = null;
        }
        this.mediaPlayer.pause();
    }

    public void skipNextPodcast() {
        pausePodcast();

        this.mediaPlayer.reset();

        PodcastEpisode episode = displayPodcastEpisode(this.podcast);

        if (episode == null) {
            return;
        }

        startPodcastEpisode(episode);
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

                if (binding == null)
                    break;

                binding.podcastSlider.setValue(currentPos);
                writeDuration(currentPos, duration);
            }

            if (binding != null) {
                Drawable playDrawable = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_play);
                binding.btnPlayPause.setImageDrawable(playDrawable);
            }
        } catch (IllegalStateException e) {
            Logs.error(this, e.getMessage());
        }
    }

    @Override
    public void onDestroyView() {

        Dialog dialog = getDialog();
        dialog.setOnShowListener(null);

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {

        if (audioSliderThread != null) {
            audioSliderThread.interrupt();
            audioSliderThread = null;
        }

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        
        binding.btnPlayPause.setOnClickListener(null);

        binding.podcastSlider.removeOnSliderTouchListener(onSliderTouch);
        
        binding = null;
        super.onDestroy();
    }
}
