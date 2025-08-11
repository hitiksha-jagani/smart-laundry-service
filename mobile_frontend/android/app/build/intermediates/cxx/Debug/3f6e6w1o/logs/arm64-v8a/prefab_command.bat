@echo off
"C:\\Program Files\\Java\\jdk-21\\bin\\java" ^
  --class-path ^
  "C:\\Users\\nencypatel\\.gradle\\caches\\modules-2\\files-2.1\\com.google.prefab\\cli\\2.1.0\\aa32fec809c44fa531f01dcfb739b5b3304d3050\\cli-2.1.0-all.jar" ^
  com.google.prefab.cli.AppKt ^
  --build-system ^
  cmake ^
  --platform ^
  android ^
  --abi ^
  arm64-v8a ^
  --os-version ^
  24 ^
  --stl ^
  c++_shared ^
  --ndk-version ^
  27 ^
  --output ^
  "C:\\Users\\nencypatel\\AppData\\Local\\Temp\\agp-prefab-staging9531296665432047487\\staged-cli-output" ^
  "C:\\Users\\nencypatel\\.gradle\\caches\\8.13\\transforms\\d644ee9b842757375fb9cebdd915ee04\\transformed\\react-android-0.79.5-debug\\prefab" ^
  "D:\\MSCIT\\summerinternship\\smart-laundry-service\\mobile_frontend\\android\\app\\build\\intermediates\\cxx\\refs\\react-native-reanimated\\262x4w40" ^
  "C:\\Users\\nencypatel\\.gradle\\caches\\8.13\\transforms\\fa03d1ffcf2d88d54ed6c7d80f87d2da\\transformed\\hermes-android-0.79.5-debug\\prefab" ^
  "C:\\Users\\nencypatel\\.gradle\\caches\\8.13\\transforms\\e80cc6deab05b24bdfe1060903f43f89\\transformed\\fbjni-0.7.0\\prefab"
