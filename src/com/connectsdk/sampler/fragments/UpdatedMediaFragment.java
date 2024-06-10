//
//  Connect SDK Sample App by LG Electronics
//
//  To the extent possible under law, the person who associated CC0 with
//  this sample app has waived all copyright and related or neighboring rights
//  to the sample app.
//
//  You should have received a copy of the CC0 legalcode along with this
//  work. If not, see http://creativecommons.org/publicdomain/zero/1.0/.
//

package com.connectsdk.sampler.fragments;



import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CheckBox;

import android.widget.SeekBar;


import com.connectsdk.sampler.R;
import com.connectsdk.sampler.util.TestResponseObject;

import com.connectsdk.service.capability.KeyControl;
import com.connectsdk.service.capability.MediaControl;
import com.connectsdk.service.capability.VolumeControl;
import com.connectsdk.service.capability.VolumeControl.MuteListener;
import com.connectsdk.service.capability.VolumeControl.VolumeListener;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.command.ServiceSubscription;
import com.connectsdk.service.sessions.LaunchSession;

import java.util.Timer;
import java.util.TimerTask;

public class UpdatedMediaFragment extends BaseFragment {
    public CheckBox muteToggleButton;
    public Button volumeUpButton;
    public Button volumeDownButton;
    public SeekBar volumeSlider;

    public Button playButton;
    public Button pauseButton;
    public Button rewindButton;
    public Button fastForwardButton;


    // ADDED FROM HERE
    public Button upButton;
    public Button leftButton;
    public Button rightButton; // ADDED RIGHT BUTTON
    public Button clickButton;
    public Button backButton;
    public Button downButton;
    public Button homeButton;

    public View trackpadView;

    boolean isDown = false;
    boolean isMoving = false;
    boolean isScroll = false;

    float startX;
    float startY;

    float lastX = Float.NaN;
    float lastY = Float.NaN;

    int scrollDx, scrollDy;
    long eventStart = 0;
    Timer timer = new Timer();
    TimerTask autoScrollTimerTask;

    // ADDED FROM HERE
    public TestResponseObject testResponse;

    private ServiceSubscription<VolumeListener> mVolumeSubscription;
    private ServiceSubscription<MuteListener> mMuteSubscription;

    public UpdatedMediaFragment() {};

    public UpdatedMediaFragment(Context context)
    {
        super(context);
        testResponse = new TestResponseObject();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.updated_media_fragment, container, false);

        muteToggleButton = (CheckBox) rootView.findViewById(R.id.muteToggle);
        volumeUpButton = (Button) rootView.findViewById(R.id.volumeUpButton);
        volumeDownButton = (Button) rootView.findViewById(R.id.volumeDownButton);

        playButton = (Button) rootView.findViewById(R.id.playButton);
        pauseButton = (Button) rootView.findViewById(R.id.pauseButton);

        rewindButton = (Button) rootView.findViewById(R.id.rewindButton);
        fastForwardButton = (Button) rootView.findViewById(R.id.fastForwardButton);

        volumeSlider = (SeekBar) rootView.findViewById(R.id.volumeSlider);
        volumeSlider.setMax(100);


        upButton = (Button) rootView.findViewById(R.id.upButton);
        leftButton = (Button) rootView.findViewById(R.id.leftButton);
        rightButton = (Button) rootView.findViewById(R.id.rightButton); // ADDED RIGHT BUTTOn
        clickButton = (Button) rootView.findViewById(R.id.clickButton);
        backButton = (Button) rootView.findViewById(R.id.backButton);
        downButton = (Button) rootView.findViewById(R.id.downButton);
        homeButton = (Button) rootView.findViewById(R.id.homeButton);

        trackpadView = rootView.findViewById(R.id.trackpadView);

        buttons = new Button[] {

                volumeUpButton,
                volumeDownButton,
                muteToggleButton,
                pauseButton,
                playButton,
                rewindButton,
                fastForwardButton,

                upButton,
                leftButton,
                clickButton,
                backButton,
                downButton,
                homeButton,
                rightButton,

        };


        return rootView;
    }

    @Override
    public void enableButtons() {
        super.enableButtons();


        volumeSlider.setEnabled(getTv().hasCapability(VolumeControl.Volume_Set));

        muteToggleButton.setEnabled(getTv().hasCapability(VolumeControl.Mute_Set));
        volumeUpButton.setEnabled(getTv().hasCapability(VolumeControl.Volume_Up_Down));
        volumeDownButton.setEnabled(getTv().hasCapability(VolumeControl.Volume_Up_Down));

        playButton.setEnabled(getTv().hasCapability(MediaControl.Play));
        pauseButton.setEnabled(getTv().hasCapability(MediaControl.Pause));
        rewindButton.setEnabled(getTv().hasCapability(MediaControl.Rewind));
        fastForwardButton.setEnabled(getTv().hasCapability(MediaControl.FastForward));

        if (getTv().hasCapability(VolumeControl.Volume_Subscribe))
            mVolumeSubscription = getVolumeControl().subscribeVolume(volumeListener);

        if (getTv().hasCapability(VolumeControl.Mute_Subscribe))
            mMuteSubscription = getVolumeControl().subscribeMute(muteListener);


        volumeUpButton.setOnClickListener(volumeChangedClickListener);
        volumeDownButton.setOnClickListener(volumeChangedClickListener);
        muteToggleButton.setOnClickListener(muteToggleClickListener);
        volumeSlider.setOnSeekBarChangeListener(volumeSeekListener);

        playButton.setOnClickListener(playClickListener);
        pauseButton.setOnClickListener(pauseClickListener);
        rewindButton.setOnClickListener(rewindClickListener);
        fastForwardButton.setOnClickListener(fastForwardClickListener);

        if (getMouseControl() != null) {
            getMouseControl().connectMouse();
        }

        if (getTv().hasCapability(KeyControl.Up)) {
            upButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getKeyControl() != null) {
                        getKeyControl().up(null);
                        testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.UpClicked);
                    }
                }
            });
        }
        else {
            disableButton(upButton);
        }

        if (getTv().hasCapability(KeyControl.Left)) {
            leftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getKeyControl() != null) {
                        getKeyControl().left(null);
                        testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.LeftClicked);
                    }
                }
            });
        }
        else {
            disableButton(leftButton);
        }

        // ADDED RIGHT BUTTON HERE //
        if (getTv().hasCapability(KeyControl.Right)) {
            rightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getKeyControl() != null) {
                        getKeyControl().right(null);
                        testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.RightClicked);
                    }
                }
            });
        }
        else {
            disableButton(rightButton);
        }
        // ADDED RIGHT BUTTON HERE //

        if (getTv().hasCapability(KeyControl.OK)) {
            clickButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getKeyControl() != null) {
                        getKeyControl().ok(null);
                        testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.Clicked);
                    }
                }
            });
        }
        else {
            disableButton(clickButton);
        }
        // TODO 함수를 채우시오


        if (getTv().hasCapability(KeyControl.Back)) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getKeyControl() != null) {
                        getKeyControl().back(null);
                    }
                }
            });
        }
        else {
            disableButton(backButton);
        }

        if (getTv().hasCapability(KeyControl.Down)) {
            downButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getKeyControl() != null) {
                        getKeyControl().down(null);
                        testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.DownClicked);
                    }
                }
            });
        }
        else {
            disableButton(downButton);
        }

        if (getTv().hasCapability(KeyControl.Home)) {
            // TODO 함수를 채우시오
            homeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getKeyControl() != null) {
                        getKeyControl().home(null);
                        testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.HomeClicked);
                    }
                }
            });
        }
        else {
            disableButton(homeButton);
        }

        trackpadView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float dx = 0, dy = 0;

                boolean wasMoving = isMoving;
                boolean wasScroll = isScroll;

                isScroll = isScroll || motionEvent.getPointerCount() > 1;

                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        isDown = true;
                        eventStart = motionEvent.getEventTime();
                        startX = motionEvent.getX();
                        startY = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        isDown = false;
                        isMoving = false;
                        isScroll = false;
                        lastX = Float.NaN;
                        lastY = Float.NaN;
                        break;
                }

                if (lastX != Float.NaN || lastY != Float.NaN) {
                    dx = Math.round(motionEvent.getX() - lastX);
                    dy = Math.round(motionEvent.getY() - lastY);
                }

                lastX = motionEvent.getX();
                lastY = motionEvent.getY();

                float xDistFromStart = Math.abs(motionEvent.getX() - startX);
                float yDistFromStart = Math.abs(motionEvent.getY() - startY);

                if (isDown && !isMoving) {
                    if (xDistFromStart > 10 && yDistFromStart > 10) {
                        isMoving = true;
                    }
                }

                if (isDown && isMoving) {
                    if (dx != 0 && dy != 0) {
                        // Scale dx and dy to simulate acceleration
                        int dxSign = dx >= 0 ? 1 : -1;
                        int dySign = dy >= 0 ? 1 : -1;

                        dx = dxSign * Math.round(Math.pow(Math.abs(dx), 1.1));
                        dy = dySign * Math.round(Math.pow(Math.abs(dy), 1.1));

                        if (!isScroll) {
                            if (getMouseControl() != null)
                                getMouseControl().move(dx, dy);
                        } else {
                            long now = SystemClock.uptimeMillis();

                            scrollDx = (int)(motionEvent.getX() - startX);
                            scrollDy = (int)(motionEvent.getY() - startY);

                            if (now - eventStart > 1000 && autoScrollTimerTask == null) {
                                Log.d("main", "starting autoscroll");
                                // start autoscrolling
                                autoScrollTimerTask = new TimerTask() {
                                    @Override
                                    public void run() {
                                        if (getMouseControl() != null)
                                            getMouseControl().scroll(scrollDx, scrollDy);
                                    }
                                };

                                timer.schedule(autoScrollTimerTask, 100, 750);
                            }
                        }
                    }
                } else if (!isDown && !wasMoving) {
                    if (getMouseControl() != null)
                        getMouseControl().click();
                } else if (!isDown && wasMoving && wasScroll) {
                    // release two fingers
                    dx = motionEvent.getX() - startX;
                    dy = motionEvent.getY() - startY;

                    if (getMouseControl() != null)
                        getMouseControl().scroll(dx, dy);
                    Log.d("main", "sending scroll " + dx + " ," + dx);
                }

                if (!isDown) {
                    isMoving = false;

                    if (autoScrollTimerTask != null) {
                        autoScrollTimerTask.cancel();
                        autoScrollTimerTask = null;

                        Log.d("main", "ending autoscroll");
                    }
                }

                return true;
            }
        });

    }

    private View.OnClickListener muteToggleClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getVolumeControl().setMute(muteToggleButton.isChecked(), null);
            if(muteToggleButton.isChecked()) {
                testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.Muted_Media);
            } else if(!muteToggleButton.isChecked()){
                testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.UnMuted_Media);
            }
        }
    };

    private View.OnClickListener volumeChangedClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.volumeDownButton:
                    getVolumeControl().volumeDown(null);
                    testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.VolumeDown);
                    break;
                case R.id.volumeUpButton:
                    getVolumeControl().volumeUp(null);
                    testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.VolumeUp);
                    break;
            }
        }
    };


    private VolumeListener volumeListener = new VolumeListener() {

        public void onSuccess(Float volume) {
            volumeSlider.setProgress((int) (volume * 100));
        }

        @Override
        public void onError(ServiceCommandError error) {
            Log.d("LG", "Error subscribing to volume: " + error);
        }
    };

    private MuteListener muteListener = new MuteListener() {

        @Override
        public void onSuccess(Boolean object) {
            muteToggleButton.setChecked(object);
        }

        @Override
        public void onError(ServiceCommandError error) {
            Log.d("LG", "Error subscribing to mute: " + error);
        }
    };

    private SeekBar.OnSeekBarChangeListener volumeSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                float fVol = (float)(progress / 100.0);
                getVolumeControl().setVolume(fVol, null);
            }
        }

        @Override public void onStartTrackingTouch(SeekBar seekBar) { }
        @Override public void onStopTrackingTouch(SeekBar seekBar) { }
    };

    private OnClickListener playClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            getMediaControl().play(null);
        }
    };

    private OnClickListener pauseClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            getMediaControl().pause(null);
        }
    };


    private OnClickListener rewindClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            getMediaControl().rewind(null);
        }
    };

    private OnClickListener fastForwardClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            getMediaControl().fastForward(null);
        }
    };

    @Override
    public void disableButtons() {

        volumeSlider.setEnabled(false);
        volumeSlider.setOnSeekBarChangeListener(null);

        if (mVolumeSubscription != null)
        {
            mVolumeSubscription.unsubscribe();
            mVolumeSubscription = null;
        }

        if (mMuteSubscription != null) {
            mMuteSubscription.unsubscribe();
            mMuteSubscription = null;
        }

        trackpadView.setOnTouchListener(null);

        super.disableButtons();
    }
}
