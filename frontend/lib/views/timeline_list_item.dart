
import 'package:extended_image/extended_image.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../models/Timeline.dart';
import '../view_models/app_view_model.dart';

import 'package:moyeo/views/timeline_detail_page.dart';

class TimelineListItem extends StatelessWidget {
  final Timeline timeline;

  const TimelineListItem({super.key, required this.timeline});

  @override
  Widget build(BuildContext context) {
    const cardHeight = 120.0;
    return Consumer<AppViewModel>(builder: (_, appViewModel, __) {
      return Container(
        margin: const EdgeInsets.only(bottom: 10, left: 5, right: 5),
        height: cardHeight,
        child: GestureDetector(
          onTap: () {
            FocusScope.of(context).unfocus();
            appViewModel.changeTitle(timeline.title);
            Navigator.pushNamed(
              context,
              '/timeline/detail/${timeline.timelineId}',
            );
          },
          child: Card(
            margin: const EdgeInsets.only(left: 12, right:12),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(10.0),
              side: const BorderSide(color: Colors.transparent, width: 1),
            ),
            elevation: 3,
            clipBehavior: Clip.antiAliasWithSaveLayer,
            child: Row(
              children: [
                Container(
            decoration: const BoxDecoration(
            gradient: LinearGradient(
              begin: Alignment.topCenter,
              end: Alignment.bottomCenter,
              colors: <Color>[
                Color(0xff4B75C9),
                Color(0xc9D16DDA),
                Color(0xffFFBB66),
                Color(0xffFF8E00)
              ],
              ),
            ),
                    width: 10
                ),
                Expanded(
                  child: Column(
                    children: [
                      Expanded(
                        child: Container(
                          // 티켓 디자인 추가
                          decoration: const BoxDecoration(
                            gradient: LinearGradient(
                                begin: Alignment.topCenter,
                                end: Alignment.bottomCenter,
                                colors: <Color>[
                                Color(0xff4B75C9),
                                Color(0xc9D16DDA),
                                Color(0xffFFBB66),
                                Color(0xffFF8E00)
                              ],
                            )
                          ),
                          padding: const EdgeInsets.only(
                              top: 10,
                              right: 10,
                              bottom: 10
                          ),
                          child: Row(
                            children: [
                              SizedBox(
                                height: 130,
                                width: 90,
                                child: ClipRRect(
                                  borderRadius: const BorderRadius.all(
                                      Radius.circular(10)),
                                  child: timeline.imageUrl.isNotEmpty
                                      ? ExtendedImage.network(
                                            timeline.imageUrl,
                                            fit: BoxFit.cover,
                                            cache: true,
                                        )
                                      : Container(
                                          color: Colors.black12,
                                          child: Image.asset(
                                            'assets/images/default_image.png',
                                            fit: BoxFit.cover,
                                          ),
                                        ),
                                ),
                              ),
                              SizedBox(width: 10),
                              Expanded(
                                child: Column(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    Expanded(
                                        child: Row(
                                          children: [
                                            const SizedBox(height: 15),
                                            const SizedBox(
                                              width: 90,
                                            ),
                                            Expanded(
                                                child: Container(
                                                  width: 80,
                                                  child: Text(
                                                    '${timeline.startPlace} ~ ${timeline.finishPlace}',
                                                    maxLines: 1,
                                                    overflow: TextOverflow.ellipsis,
                                                    textAlign: TextAlign.right,
                                                    style: const TextStyle(color: Colors.white),
                                                  ),
                                                )
                                            )
                                          ],
                                        )
                                    ),
                                    Expanded(
                                      child: Row(
                                        children: [
                                          const SizedBox(height: 15),
                                          const SizedBox(
                                            width: 90,
                                            child: Text(
                                              "T   I  T  L  E",
                                              style: TextStyle(
                                                  color: Colors.white,
                                                  fontWeight: FontWeight.bold),
                                            ),
                                          ),
                                          Expanded(
                                            child:Container(
                                              width: 80,
                                              padding: EdgeInsets.only(left: 10.0),
                                              decoration: BoxDecoration(
                                                color: Colors.white,
                                                borderRadius: BorderRadius.circular(10)
                                              ),
                                              child:Text(
                                                timeline.title,
                                                maxLines: 1,
                                                overflow: TextOverflow.ellipsis,
                                              )
                                            ),
                                          ),
                                        ],
                                      ),
                                    ),
                                    Expanded(
                                      child: Row(
                                        children: [
                                          const SizedBox(
                                            width: 90,
                                            child: Text(
                                              "TRAVELER",
                                              style: TextStyle(
                                                  color: Colors.white,
                                                  fontWeight: FontWeight.bold),
                                            ),
                                          ),
                                          Expanded(
                                            child:Container(
                                              width: 80,
                                              padding: EdgeInsets.only(left: 10.0),
                                              decoration: BoxDecoration(
                                                  color: Colors.white,
                                                  borderRadius: BorderRadius.circular(10)
                                              ),
                                              child: Text(timeline.nickname),
                                            )
                                          ),
                                        ],
                                      ),
                                    ),
                                    Expanded(
                                      child: Row(
                                        children: [
                                          const SizedBox(
                                            width: 90,
                                            child: Text("DURATION",
                                                style: TextStyle(
                                                  color: Colors.white,
                                                    fontWeight:
                                                        FontWeight.bold)),
                                          ),
                                          Expanded(
                                            child:Container(
                                              width: 80,
                                              padding: EdgeInsets.only(left: 10.0),
                                              decoration: BoxDecoration(
                                                  color: Colors.white,
                                                  borderRadius: BorderRadius.circular(10)
                                              ),
                                              child: Text(
                                                  '${timeline.createTime} ~ ${timeline.finishTime}'),
                                            )
                                          )
                                        ],
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                            ],
                          ),
                        ),
                      ),
                    ],
                  ),
                )
              ],
            ),
          ),
        ),
      );
    });
  }
}
