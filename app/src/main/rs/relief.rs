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
// Store the input allocation
rs_allocation inputAllocation;
int value = 100;

uchar4 __attribute__((kernel)) magnify(uchar4 in, int x, int y) {


  uchar4 cur= rsGetElementAt_uchar4(inputAllocation, x, y);
  cur = cur*value;
  if(x==0){
    return cur;
  }
  uchar4 fomr=rsGetElementAt_uchar4(inputAllocation,x-1,y);
  cur=fomr-cur+128;

   return cur*value;
}
void init(){
}
void ok(){}