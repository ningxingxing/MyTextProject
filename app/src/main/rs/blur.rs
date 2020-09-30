#pragma version(1)

// The java_package_name directive needs to use your Activity's package path
#pragma rs java_package_name(com.example.mytestproject)
#pragma rs_fp_relaxed
// Store the input allocation

//int radius=0;

float areaSize = 50.0f;
float offset = 1.0;
int32_t gWidth;
int32_t gHeight;
rs_allocation gIn;
float radius;
float _Gradient;
float _GoldenRot;
float _Params;
float _PixelSize;

float clickX;
float clickY;

uchar4 __attribute__((kernel)) invert(uchar4 in, uint32_t x, uint32_t y) {

      float dt = (float)sqrt(pow(x-clickX, 2) + pow(y-clickY, 2));
         float _radius = 0;
         int circle = gWidth / 6;
         if(dt > circle){
             _radius = dt * 4 / circle - 4;
             if( _radius >3 ){
               _radius = 3;
             }
         }else{
              radius = 0;
         }

         uint32_t x1 = min((int32_t)(x + 1 * _radius), gWidth - 1);
         uint32_t x2 = max((int32_t)(x - 1 * _radius), 0);
         uint32_t y1 = min((int32_t)(y + 1 * _radius), gHeight - 1);
         uint32_t y2 = max((int32_t)(y - 1 * _radius), 0);

         uint32_t x3 = min((int32_t)(x + 2.0 * _radius), gWidth - 1);
         uint32_t x4 = max((int32_t)(x - 2.0 * _radius ), 0);
         uint32_t y3 = min((int32_t)(y + 2.0 * _radius ), gHeight - 1);
         uint32_t y4 = max((int32_t)(y - 2.0 * _radius ), 0);

         float4 p00 = rsUnpackColor8888(in);
         float4 p01 = rsUnpackColor8888(*((uchar4 *)rsGetElementAt(gIn, x, y))) * 4.0;
         float4 p11 = rsUnpackColor8888(*((uchar4 *)rsGetElementAt(gIn, x1, y1))) * 2.0;
         float4 p12 = rsUnpackColor8888(*((uchar4 *)rsGetElementAt(gIn, x1, y2))) * 2.0;
         float4 p21 = rsUnpackColor8888(*((uchar4 *)rsGetElementAt(gIn, x2, y1))) * 2.0;
         float4 p22 = rsUnpackColor8888(*((uchar4 *)rsGetElementAt(gIn, x2, y2))) * 2.0;

         float4 p03 = rsUnpackColor8888(*((uchar4 *)rsGetElementAt(gIn, x, y3)));
         float4 p04 = rsUnpackColor8888(*((uchar4 *)rsGetElementAt(gIn, x, y4)));
         float4 p30 = rsUnpackColor8888(*((uchar4 *)rsGetElementAt(gIn, x3, y)));
         float4 p40 = rsUnpackColor8888(*((uchar4 *)rsGetElementAt(gIn, x4, y)));

         float4 sum =p01 + p11 + p12 + p21 + p22 + p03 + p04 + p30 + p40;
         float4 result = sum / 16.0;

         if(_radius!=0&& dt>circle && gWidth!=0){
             return rsPackColorTo8888(result.r, result.g, result.b, p00.a);
         }else{
             return rsPackColorTo8888(p00.r, p00.g, p00.b, p00.a);
         }


/*
    float4 p11 = rsUnpackColor8888(*((uchar4 *)rsGetElementAt(gIn, x1, y1))) * 2.0;

    float4 rot = 5.0;
    float4 accumulator = 0.0;
    float4 divisor = 0.0;

    float4 r = 1.0;
    float4 center = p11 * 2.0 - 1.0 + offset; // [0,1] -> [-1,1]
    float4 dd =  dot(center, center) * areaSize;

    float4 angle = radius * dd;

    for (int j = 0; j < 10; j++)
    {
        r += 1.0 / r;
        angle = rot*angle;
        float4 bokeh = p11 + _PixelSize * (r - 1.0) * angle;
        accumulator += bokeh * bokeh;
        divisor += bokeh;
    }

     float4 result1 = accumulator / divisor;

     float4 f4 = rsUnpackColor8888(in);
*/
   // return rsPackColorTo8888(result1.r, result1.g, result1.b, f4.a);
}


void init(){
}
