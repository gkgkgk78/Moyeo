import 'dart:io';

import 'package:camera/camera.dart';
import 'package:extended_image/extended_image.dart';
import 'package:flutter/cupertino.dart';
import 'package:smooth_page_indicator/smooth_page_indicator.dart';

import '../utils/app_scroll_behavior.dart';

class ImagesPageView extends StatelessWidget {
  final List<String> listImageUrl;
  final List<XFile> listXFile;
  final BoxFit boxFit;
  final List<Widget> imageList = <Widget>[];

  ImagesPageView(
      {super.key,
      this.listImageUrl = const [],
      this.listXFile = const [],
      this.boxFit = BoxFit.cover}) {
    if (listImageUrl.isNotEmpty) {
      for (var url in listImageUrl) {
        imageList.add(ExtendedImage.network(
          url,
          cache: true,
          fit: boxFit,
        ));
      }
    } else {
      for (var xFile in listXFile) {
        imageList.add(
          ExtendedImage.file(
            File(xFile.path),
            fit: boxFit,
          ),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final controller = PageController(initialPage: 0);

    return imageList.isNotEmpty
        ? Stack(
            children: [
              PageView(
                scrollBehavior: AppScrollBehavior(),
                controller: controller,
                children: imageList,
              ),
              Align(
                alignment: const AlignmentDirectional(0, -0.9),
                child: Padding(
                  padding: const EdgeInsetsDirectional.fromSTEB(0, 0, 0, 10),
                  child: SmoothPageIndicator(
                    controller: controller,
                    count: imageList.length,
                    onDotClicked: (index) {
                      controller.animateToPage(
                        index,
                        duration: const Duration(milliseconds: 300),
                        curve: Curves.ease,
                      );
                    },
                  ),
                ),
              ),
            ],
          )
        : Container();
  }
}
