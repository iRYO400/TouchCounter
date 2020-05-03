package workshop.akbolatss.tools.touchcounter.utils.logging;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import timber.log.Timber;
import workshop.akbolatss.tools.touchcounter.BuildConfig;

public class TimberLogImplementation {

    public static void init() {
        if (BuildConfig.DEBUG)
            Timber.plant(new DevelopmentDebugTree());
        else
            Timber.plant(new ProductionTree());
    }

    public static class DevelopmentDebugTree extends Timber.DebugTree {
        @Override
        protected @Nullable String createStackElementTag(@NotNull StackTraceElement element) {
            return String.format("TouchCounter:C:%s:%s",
                    super.createStackElementTag(element),
                    element.getLineNumber());
        }
    }

    public static class ProductionTree extends Timber.Tree {

        @Override
        protected void log(int priority, @Nullable String tag, @NotNull String message, @Nullable Throwable t) {
//            if (priority == Log.ERROR || priority == Log.WARN)
//                FabricImplementation.sendStackTrace(t);
        }
    }
}
