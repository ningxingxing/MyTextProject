/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#pragma version(1)
#pragma rs java_package_name(com.coocent.photos.imagefilters)
#pragma rs_fp_relaxed

float saturationValue = 0.f;
const static float3 gMonoMult = {0.299f, 0.587f, 0.114f};

uchar4 __attribute__((kernel)) invert(uchar4 in, uint32_t x, uint32_t y) {
   float4 f4 = rsUnpackColor8888(in);
   float3 result = dot(f4.rgb, gMonoMult);
   result = mix(result, f4.rgb, saturationValue);
   return rsPackColorTo8888(result);
}
