# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontoptimize
-dontpreverify

# This is needed to allow release builds from android studio.
-ignorewarnings

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep enough data for stack traces
-keepnames class **
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable,*Annotation*

# Keep the static fields of referenced inner classes of auto-generated R classes, in case we
# access those fields by reflection (e.g. EmojiMarkup)
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Keep all libraries
-keep class android.support.** { *; }
-keep class org.joda.** { *; }
-keep class io.codetail.** { *; }
-keep class ezvcard.** { *; }
-keep class com.makeramen.** { *; }
-keep class com.squareup.** { *; }
-keep class com.konifar.** { *; }
-keep class com.google.** { *; }

# keep crashlytics
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**