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


public class ColorsFragment extends Fragment implements WordsAdpaters.OnClickItemRecyclerView,AudioManager.OnAudioFocusChangeListener {

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




    public ColorsFragment() {
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
        View view= inflater.inflate(R.layout.fragment_colors, container, false);

        ArrayList<Words> ColorsArraylist=new ArrayList<>();
        ColorsArraylist.add(new Words(R.raw.color_red,"weṭeṭṭi","red",R.drawable.color_red));
        ColorsArraylist.add(new Words(R.raw.color_green,"chokokki","green",R.drawable.color_green));
        ColorsArraylist.add(new Words(R.raw.color_brown,"ṭakaakki","brown",R.drawable.color_brown));
        ColorsArraylist.add(new Words(R.raw.color_gray,"ṭopoppi","gray",R.drawable.color_gray));
        ColorsArraylist.add(new Words(R.raw.color_black,"kululli","black",R.drawable.color_black));
        ColorsArraylist.add(new Words(R.raw.color_white,"kelelli","white",R.drawable.color_white));
        ColorsArraylist.add(new Words(R.raw.color_dusty_yellow,"ṭopiisә","dusty yellow",R.drawable.color_dusty_yellow));
        ColorsArraylist.add(new Words(R.raw.color_mustard_yellow,"chiwiiṭә","mustard yellow",R.drawable.color_mustard_yellow));



        RecyclerView recyclerView=view.findViewById(R.id.colorsRecyclerView);
        WordsAdpaters adapter=new WordsAdpaters(this,ColorsArraylist,R.color.colors_Color);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager manager=new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(manager);

        return view;



    }


    @Override
    public void onStop() {
        super.onStop();
        releaseMediaPlayerResources();
    }
}