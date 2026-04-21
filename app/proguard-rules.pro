# Kotlinx Serialization - keep @Serializable classes
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keep,includedescriptorclasses class com.nav3cookbook.sample.**$$serializer { *; }
-keepclassmembers class com.nav3cookbook.sample.** {
    *** Companion;
}
-keepclasseswithmembers class com.nav3cookbook.sample.** {
    kotlinx.serialization.KSerializer serializer(...);
}
