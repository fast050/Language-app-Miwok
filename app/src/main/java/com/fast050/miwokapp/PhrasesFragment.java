package com.fast050.miwokapp;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class PhrasesFragment extends Fragment implements WordsAdpaters.OnClickItemRecyclerView,AudioManager.OnAudioFocusChangeListener  {


    MediaPlayer mediaPlayer;
    AudioManager mAudioManager;
    boolean mPlaybackDelayed = false;
    boolean mResumeOnFocusGain;
    final Object mFocusLock = new Object();
    AudioAttributes mPlaybackAttributes ;

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            // Now that the sound file has finished playing, release the media player resources.
            releaseMediaPlayerResources();
        }
    };

    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                // The AUDIOFOCUS_LOSS_TRANSIENT case means that we've lost audio focus for a
                // short amount of time. The AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK case means that
                // our app is allowed to continue playing sound but at a lower volume. We'll treat
                // both cases the same way because our app is playing short sound files.

                // Pause playback and reset player to the start of the file. That way, we can
                // play the word from the beginning when we resume playback.
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // The AUDIOFOCUS_GAIN case means we have regained focus and can resume playback.
                mediaPlayer.start();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // The AUDIOFOCUS_LOSS case means we've lost audio focus and
                // Stop playback and clean up resources
                releaseMediaPlayerResources();
            }
        }
    };

    @Override
    public void OnClickItem(int position, List<Words> list) {

        Words words = list.get(position);
        mediaPlayer = MediaPlayer.create(getActivity(), words.getSoundResources());


        //here is Audio Focus
        if(mediaPlayer!=null)
        {
            //**AudioFocus start:
            // initialization of the audio attributes and focus request
            mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
            mPlaybackAttributes = null;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                mPlaybackAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build();

                AudioFocusRequest mFocusRequest;
                mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                        .setAudioAttributes(mPlaybackAttributes)
                        .setAcceptsDelayedFocusGain(true)
                        .setWillPauseWhenDucked(true)
                        .setOnAudioFocusChangeListener(this)
                        .build();


                // MediaPlayer mMediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioAttributes(mPlaybackAttributes);
                //final Object mFocusLock = new Object();

                // boolean mPlaybackDelayed = false;

                // requesting audio focus
                int res = mAudioManager.requestAudioFocus(mFocusRequest);
                synchronized (mFocusLock) {
                    if (res == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                        mPlaybackDelayed = false;
                    } else if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        mPlaybackDelayed = false;
                        // playbackNow();
                        mediaPlayer.start();
                    } else if (res == AudioManager.AUDIOFOCUS_REQUEST_DELAYED) {
                        mPlaybackDelayed = true;
                    }

                }

            } else
            {
                int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                        AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    // We have audio focus now.

                    // Create and setup the {@link MediaPlayer} for the audio resource associated
                    // with the current word

                    // Start the audio file
                    mediaPlayer.start();

                    // Setup a listener on the media player, so that we can stop and release the
                    // media player once the sound has finished playing.
                    mediaPlayer.setOnCompletionListener(mCompletionListener);
                }

            }

            //**AudioFocus finish:

        }


    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if(mediaPlayer!=null) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    if (mPlaybackDelayed || mResumeOnFocusGain) {
                        synchronized (mFocusLock) {
                            mPlaybackDelayed = false;
                            mResumeOnFocusGain = false;
                        }
                        //     playbackNow();
                        mediaPlayer.start();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    synchronized (mFocusLock) {
                        // this is not a transient loss, we shouldn't automatically resume for now
                        mResumeOnFocusGain = false;
                        mPlaybackDelayed = false;
                    }
                    //  pausePlayback();
                    mediaPlayer.pause();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    // we handle all transient losses the same way because we never duck audio books
                    synchronized (mFocusLock) {
                        // we should only resume if playback was interrupted
                        mResumeOnFocusGain = mediaPlayer.isPlaying();
                        mPlaybackDelayed = false;
                    }
                    //   pausePlayback();
                    mediaPlayer.pause();
                    break;
            }
        }
    }

    public  void releaseMediaPlayerResources()
    {
        if(mediaPlayer!=null)
        {
            mediaPlayer.release();
            mediaPlayer=null;

            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
                mAudioManager.abandonAudioFocus(this);
            else
                mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }


    public PhrasesFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_phrases, container, false);

        ArrayList<Words> PhrasesArraylist = new ArrayList<>();
        PhrasesArraylist.add(new Words(R.raw.phrase_where_are_you_going,"minto wuksus", "Where are you going?"));
        PhrasesArraylist.add(new Words( R.raw.phrase_what_is_your_name,"tinnә oyaase'nә", "What is your name?"));
        PhrasesArraylist.add(new Words( R.raw.phrase_my_name_is,"oyaaset...", "My name is..."));
        PhrasesArraylist.add(new Words(R.raw.phrase_how_are_you_feeling,"michәksәs?", "How are you feeling?"));
        PhrasesArraylist.add(new Words(R.raw.phrase_im_feeling_good,"kuchi achit", "I’m feeling good."));
        PhrasesArraylist.add(new Words(R.raw.phrase_are_you_coming,"әәnәs'aa?", "Are you coming?"));
        PhrasesArraylist.add(new Words(R.raw.phrase_yes_im_coming,"hәә’ әәnәm", "Yes, I’m coming."));
        PhrasesArraylist.add(new Words(R.raw.phrase_im_coming,"әәnәm", "I’m coming."));
        PhrasesArraylist.add(new Words( R.raw.phrase_lets_go,"yoowutis", "Let’s go."));
        PhrasesArraylist.add(new Words(R.raw.phrase_come_here,"әnni'nem", "Come here."));


        RecyclerView recyclerView = view.findViewById(R.id.phrasesRecyclerView);
        WordsAdpaters adapter = new WordsAdpaters(this, PhrasesArraylist, R.color.phrases_Color);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        releaseMediaPlayerResources();
    }
}