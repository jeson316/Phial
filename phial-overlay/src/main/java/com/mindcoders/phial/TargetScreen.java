package com.mindcoders.phial;

import android.app.Activity;

/**
 * Class is used to abstract from activity,
 * since in future we might provide functionality to show options for Fragment as well
 */
public class TargetScreen {
    private final Class<? extends Activity> target;
    private final String scope;


    TargetScreen(Class<? extends Activity> target, String scope) {
        this.target = target;
        this.scope = scope;
    }

    public static TargetScreen forActivity(Class<? extends Activity> target) {
        return new TargetScreen(target, null);
    }

    public static TargetScreen forScope(String scope) {
        return new TargetScreen(null, scope);
    }

    public Class<? extends Activity> getTargetActivity() {
        return target;
    }

    public String getScope() {
        return scope;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TargetScreen that = (TargetScreen) o;

        if (target != null ? !target.equals(that.target) : that.target != null) return false;
        return scope != null ? scope.equals(that.scope) : that.scope == null;
    }

    @Override
    public int hashCode() {
        int result = target != null ? target.hashCode() : 0;
        result = 31 * result + (scope != null ? scope.hashCode() : 0);
        return result;
    }
}


