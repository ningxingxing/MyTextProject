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
rs_allocation inputAllocation;

// Magnifying
// TODO: here, some checks should be performed to prevent atX and atY to be < 0, as well
//   as them to not be greater than width and height
int atX;
int atY;
float radius;
float scale; // The scale is >= 1

uchar4 __attribute__((kernel)) magnify(uchar4 in, int x, int y) {

    // Calculates the distance between the touched point and the current kernel
    // iteration pixel coordinated
    // Reference: http://math.stackexchange.com/a/198769
    float pointDistanceFromCircleCenter = sqrt(pow((float)(x - atX),2) + pow((float)(y - atY),2));


    // Is this pixel outside the magnify radius?
    if(radius < pointDistanceFromCircleCenter)
    {
        // In this case, just copy the original image
        return in;
    }


    // If the point is inside the magnifying inner radius, draw the magnified image

    // Calculates the current distance from the chosen magnifying center
    float diffX = x - atX;
    float diffY = y - atY;

    // Scales down the distance accordingly to scale and returns the original coordinates
    int originalX = atX + round(diffX / scale);
    int originalY = atY + round(diffY / scale);

    // Return the original image pixel at the calculated coordinates
    return rsGetElementAt_uchar4(inputAllocation, originalX, originalY);
}

