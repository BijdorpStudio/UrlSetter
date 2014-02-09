package com.jensdriller.libs.undobar;

import android.app.Activity;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.emartynov.android.app.urlsetter.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewPropertyAnimator;

import static com.nineoldandroids.view.ViewHelper.setAlpha;
import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

public final class UndoBar
{

    /**
     * Listener for actions of the undo bar.
     */
    public static interface Listener
    {
        /**
         * Will be fired when the undo bar disappears without being actioned.
         */
        void onHide();

        /**
         * Will be fired when the undo button is pressed.
         */
        void onUndo( Parcelable token );
    }

    /**
     * Default duration in milliseconds the undo bar will be displayed.
     */
    public static final int DEFAULT_DURATION = 5000;

    /**
     * Default duration in milliseconds of the undo bar show and hide animation.
     */
    public static final int DEFAULT_ANIMATION_DURATION = 300;

    private final Activity mActivity;

    private final UndoBarView mView;

    private final ViewPropertyAnimator mViewAnimator;

    private final Handler mHandler = new Handler();

    private final Runnable mHideRunnable = new Runnable()
    {

        @Override
        public void run()
        {
            hide( true );
            safelyNotifyOnHide();
        }
    };

    private final OnClickListener mOnUndoClickListener = new OnClickListener()
    {

        @Override
        public void onClick( View v )
        {
            hide( true );
            safelyNotifyOnUndo();
        }
    };

    private Listener mUndoListener;

    private Parcelable mUndoToken;

    private CharSequence mUndoMessage;

    private int mDuration = DEFAULT_DURATION;

    private int mAnimationDuration = DEFAULT_ANIMATION_DURATION;

    public UndoBar( Activity activity )
    {
        mActivity = activity;
        mView = getView( activity );
        mView.setOnUndoClickListener( mOnUndoClickListener );
        mViewAnimator = animate( mView );

        hide( false );
    }

    /**
     * Sets the message to be displayed on the left of the undo bar.
     */
    public void setMessage( CharSequence message )
    {
        mUndoMessage = message;
    }

    /**
     * Sets the message to be displayed on the left of the undo bar.
     */
    public void setMessage( int messageResId )
    {
        mUndoMessage = mActivity.getString( messageResId );
    }

    /**
     * Sets the {@link com.jensdriller.libs.undobar.UndoBar.Listener UndoBar.Listener}.
     */
    public void setListener( Listener undoListener )
    {
        mUndoListener = undoListener;
    }

    /**
     * Sets a {@link android.os.Parcelable} token to the undo bar which will be returned in
     * the {@link com.jensdriller.libs.undobar.UndoBar.Listener UndoBar.Listener}.
     */
    public void setUndoToken( Parcelable undoToken )
    {
        mUndoToken = undoToken;
    }

    /**
     * Sets the duration the undo bar will be shown.<br>
     * Default is {@link #DEFAULT_DURATION}.
     *
     * @param duration in milliseconds
     */
    public void setDuration( int duration )
    {
        mDuration = duration;
    }

    /**
     * Sets the duration of the animation for showing and hiding the undo bar.<br>
     *
     * @param animationDuration in milliseconds
     */
    public void setAnimationDuration( int animationDuration )
    {
        mAnimationDuration = animationDuration;
    }

    /**
     * Calls {@link #show(boolean)} with {@code shouldAnimate = true}.
     */
    public void show()
    {
        show( true );
    }

    /**
     * Shows the {@link com.jensdriller.libs.undobar.UndoBar}.
     *
     * @param shouldAnimate whether the {@link com.jensdriller.libs.undobar.UndoBar} should animate in
     */
    public void show( boolean shouldAnimate )
    {
        mView.setMessage( mUndoMessage );

        mHandler.removeCallbacks( mHideRunnable );
        mHandler.postDelayed( mHideRunnable, mDuration );

        mView.setVisibility( View.VISIBLE );
        if ( shouldAnimate )
        {
            animateIn();
        }
        else
        {
            setAlpha( mView, 1 );
        }
    }

    /**
     * Calls {@link #hide(boolean)} with {@code shouldAnimate = true}.
     */
    public void hide()
    {
        hide( true );
    }

    /**
     * Hides the {@link com.jensdriller.libs.undobar.UndoBar}.
     *
     * @param shouldAnimate whether the {@link com.jensdriller.libs.undobar.UndoBar} should animate out
     */
    public void hide( boolean shouldAnimate )
    {
        mHandler.removeCallbacks( mHideRunnable );

        if ( shouldAnimate )
        {
            animateOut();
        }
        else
        {
            setAlpha( mView, 0 );
            mView.setVisibility( View.GONE );
            mUndoMessage = null;
            mUndoToken = null;
        }
    }

    /**
     * Performs the actual show animation.
     */
    private void animateIn()
    {
        mViewAnimator.cancel();
        mViewAnimator.alpha( 1 )//
            .setDuration( mAnimationDuration )//
            .setListener( null );
    }

    /**
     * Performs the actual hide animation.
     */
    private void animateOut()
    {
        mViewAnimator.cancel();
        mViewAnimator.alpha( 0 )//
            .setDuration( mAnimationDuration )//
            .setListener( new AnimatorListenerAdapter()
            {

                @Override
                public void onAnimationEnd( Animator animation )
                {
                    mView.setVisibility( View.GONE );
                    mUndoMessage = null;
                    mUndoToken = null;
                }
            } );
    }

    /**
     * Notifies listener if available.
     */
    private void safelyNotifyOnHide()
    {
        if ( mUndoListener != null )
        {
            mUndoListener.onHide();
        }
    }

    /**
     * Notifies listener if available.
     */
    private void safelyNotifyOnUndo()
    {
        if ( mUndoListener != null )
        {
            mUndoListener.onUndo( mUndoToken );
        }
    }

    /**
     * Checks if there is already an {@link UndoBarView} instance added to the
     * given {@link android.app.Activity}.<br>
     * If {@code true}, returns that instance.<br>
     * If {@code false}, inflates a new {@link UndoBarView} and returns it.
     */
    private UndoBarView getView( Activity activity )
    {
        ViewGroup rootView = (ViewGroup) activity.findViewById( android.R.id.content );
        UndoBarView undoBarView = (UndoBarView) rootView.findViewById( R.id.undoBar );
        if ( undoBarView == null )
        {
            undoBarView = (UndoBarView) LayoutInflater.from( activity ).inflate( R.layout.undo_bar, rootView, false );
            rootView.addView( undoBarView );
        }
        return undoBarView;
    }

    public static class Builder
    {

        private final Activity mActivity;

        private CharSequence mUndoMessage;

        private Listener mUndoListener;

        private Parcelable mUndoToken;

        private int mDuration = DEFAULT_DURATION;

        private int mAnimationDuration = DEFAULT_ANIMATION_DURATION;

        /**
         * Constructor using the {@link android.app.Activity} in which the undo bar will be
         * displayed
         */
        public Builder( Activity activity )
        {
            mActivity = activity;
        }

        /**
         * Sets the message to be displayed on the left of the undo bar.
         */
        public Builder setMessage( int messageResId )
        {
            mUndoMessage = mActivity.getString( messageResId );
            return this;
        }

        /**
         * Sets the message to be displayed on the left of the undo bar.
         */
        public Builder setMessage( CharSequence message )
        {
            mUndoMessage = message;
            return this;
        }

        /**
         * Sets the {@link com.jensdriller.libs.undobar.UndoBar.Listener UndoBar.Listener}.
         */
        public Builder setListener( Listener undoListener )
        {
            mUndoListener = undoListener;
            return this;
        }

        /**
         * Sets a {@link android.os.Parcelable} token to the undo bar which will be
         * returned in the {@link com.jensdriller.libs.undobar.UndoBar.Listener UndoBar.Listener}.
         */
        public Builder setUndoToken( Parcelable undoToken )
        {
            mUndoToken = undoToken;
            return this;
        }

        /**
         * Sets the duration the undo bar will be shown.<br>
         * Default is {@link #DEFAULT_DURATION}.
         *
         * @param duration in milliseconds
         */
        public Builder setDuration( int duration )
        {
            mDuration = duration;
            return this;
        }

        /**
         * Sets the duration of the animation for showing and hiding the undo
         * bar.<br>
         *
         * @param animationDuration in milliseconds
         */
        public Builder setAnimationDuration( int animationDuration )
        {
            mAnimationDuration = animationDuration;
            return this;
        }

        /**
         * Creates an {@link com.jensdriller.libs.undobar.UndoBar} instance with this Builder's
         * configuration.
         */
        public UndoBar create()
        {
            UndoBar undoBarController = new UndoBar( mActivity );
            undoBarController.setListener( mUndoListener );
            undoBarController.setUndoToken( mUndoToken );
            undoBarController.setMessage( mUndoMessage );
            undoBarController.setDuration( mDuration );
            undoBarController.setAnimationDuration( mAnimationDuration );
            return undoBarController;
        }

        /**
         * Calls {@link #show(boolean)} with {@code shouldAnimate = true}.
         */
        public void show()
        {
            show( true );
        }

        /**
         * Shows the {@link com.jensdriller.libs.undobar.UndoBar} with this Builder's configuration.
         *
         * @param shouldAnimate whether the {@link com.jensdriller.libs.undobar.UndoBar} should animate in and out.
         */
        public void show( boolean shouldAnimate )
        {
            create().show( shouldAnimate );
        }
    }
}
