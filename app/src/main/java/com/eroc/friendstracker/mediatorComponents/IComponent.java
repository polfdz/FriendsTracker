package com.eroc.friendstracker.mediatorComponents;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;

/**
 * Created by Pol on 06/11/2015.
 */
public interface IComponent {
        public Fragment getViewFragment(); //change name - fragments colision
        //public String getName();
        //public Drawable getIcon();
        void setMediator(IMediator mediator);
}
