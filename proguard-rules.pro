-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-keep class com.android.vending.billing.**

-keepclassmembers public class * extends com.gianlu.commonutils.Tutorial.BaseTutorial {
   public <init>(...);
}

-dontwarn com.gianlu.commonutils.Preferences.PreferencesBillingHelper
-dontwarn com.gianlu.commonutils.Preferences.PreferencesBillingHelper$*