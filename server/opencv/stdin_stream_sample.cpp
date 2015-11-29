#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <cv.h>
#include <highgui.h>

using namespace cv;

#if defined(_MSC_VER) || defined(WIN32)  || defined(_WIN32) || defined(__WIN32__) \
  || defined(WIN64)    || defined(_WIN64) || defined(__WIN64__)
# include <io.h>
# include <fcntl.h>
# define SET_BINARY_MODE(handle) setmode(handle, O_BINARY)
#else
# define SET_BINARY_MODE(handle) ((void)0)
#endif

#define BUFSIZE 10240
int main ( int argc, char **argv )
{

  SET_BINARY_MODE(fileno(stdin));
  std::vector<char> data;
  bool skip=true;
  bool imgready=false;
  bool ff=false;
  int readbytes=-1;

  while (1)
    {
	  // for pattern mathicng
	  double min_val, max_val;
	  CvPoint min_loc, max_loc;
	  CvSize dst_size;
	  IplImage *src_img, *tmp_img, *dst_img;
      tmp_img = cvLoadImage (argv[1], CV_LOAD_IMAGE_COLOR);

	  char ca[BUFSIZE];
	  uchar c;
	  if (readbytes!=0)
        {
		  readbytes=read(fileno(stdin),ca,BUFSIZE);
		  for(int i=0;i<readbytes;i++)
            {
			  c=ca[i];
			  if(ff && c==(uchar)0xd8)
                {
				  skip=false;
				  data.push_back((uchar)0xff);
                }
			  if(ff && c==0xd9)
                {
				  imgready=true;
				  data.push_back((uchar)0xd9);
				  skip=true;
                }
			  ff=c==0xff;
			  if(!skip)
                {
				  data.push_back(c);
                }
			  if(imgready)
                {
				  if(data.size()!=0)
                    {
					  cv::Mat data_mat(data);
					  cv::Mat frame(imdecode(data_mat,1));
					  cv::Mat cppMat = cv::imread("image.bmp");
					  IplImage iplImage = cppMat;
					  src_img = &(iplImage);

					  std::cout << "aho" <<std::endl;

					  // (1)探索画像全体に対して，テンプレートのマッチング値（指定した手法に依存）を計算
					  dst_size = cvSize (src_img->width - tmp_img->width + 1, src_img->height - tmp_img->height + 1);
					  dst_img = cvCreateImage (dst_size, IPL_DEPTH_32F, 1);
					  cvMatchTemplate (src_img, tmp_img, dst_img, CV_TM_CCOEFF_NORMED);
					  cvMinMaxLoc (dst_img, &min_val, &max_val, &min_loc, &max_loc, NULL);

					  // (2)テンプレートに対応する位置に矩形を描画
					  cvRectangle (src_img, max_loc,
								   cvPoint (max_loc.x + tmp_img->width, max_loc.y + tmp_img->height), CV_RGB (255, 0, 0), 3);
					  cvNamedWindow ("Image", 1);
					  cvShowImage ("Image", src_img);
					  cvWaitKey (0);

					  cvDestroyWindow ("Image");
					  cvReleaseImage (&src_img);
					  cvReleaseImage (&tmp_img);
					  cvReleaseImage (&dst_img);

					  //					  imshow("frame",frame);
					  //					  waitKey(1);

                    }else
                    {
					  printf("warning");
                    }
				  imgready=false;
				  skip=true;
				  data.clear();
                }
            }
        }
	  else
        {
		  throw std::string("zero byte read");
        }
    }
}
