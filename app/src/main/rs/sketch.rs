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

float size = 1;

uchar4 __attribute__((kernel)) invert(uchar4 in, uint32_t x, uint32_t y) {
   float4 f4 = rsUnpackColor8888(in);

   float r = f4.r*size;
   float g = f4.g*size;
   float b = f4.b*size;

   if((r+g+b)*255/3>=100){
       return rsPackColorTo8888(1, 1, 1, f4.a);
   }else{
       return rsPackColorTo8888(0, 0, 0, f4.a);
   }
}