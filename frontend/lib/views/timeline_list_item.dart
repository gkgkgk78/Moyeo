import 'package:danim/models/Timeline.dart';
import 'package:extended_image/extended_image.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../view_models/app_view_model.dart';

class TimelineListItem extends StatelessWidget {
  final Timeline timeline;

  const TimelineListItem({super.key, required this.timeline});

  @override
  Widget build(BuildContext context) {
    const cardHeight = 140.0;
    return Consumer<AppViewModel>(builder: (_, appViewModel, __) {
      return Container(
        margin: const EdgeInsets.only(bottom: 5, left: 5, right: 5),
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
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(10.0),
              side: const BorderSide(color: Colors.black45, width: 1.5),
            ),
            clipBehavior: Clip.antiAliasWithSaveLayer,
            child: Row(
              children: [
                const SizedBox(width: 20),
                Expanded(
                  child: Column(
                    children: [
                      Container(
                          height: cardHeight * 0.2,
                          decoration: BoxDecoration(
                            color: Theme.of(context).primaryColor,
                            borderRadius: const BorderRadius.only(
                              bottomLeft: Radius.circular(20),
                            ),
                          ),
                          child: Row(
                            mainAxisSize: MainAxisSize.max,
                            mainAxisAlignment: MainAxisAlignment.start,
                            crossAxisAlignment: CrossAxisAlignment.center,
                            children: [
                              const SizedBox(width: 30),
                              const Text(
                                "Danim",
                                style: TextStyle(color: Colors.white),
                              ),
                              const SizedBox(width: 10),
                              const Icon(
                                Icons.flight_takeoff,
                                color: Colors.white,
                              ),
                              Expanded(
                                  child: Text(
                                '${timeline.startPlace} ~ ${timeline.finishPlace}',
                                maxLines: 1,
                                overflow: TextOverflow.ellipsis,
                                textAlign: TextAlign.right,
                                style: const TextStyle(color: Colors.white),
                              )),
                              const SizedBox(width: 30)
                            ],
                          )),
                      Expanded(
                        child: Container(
                          // 티켓 디자인 추가
                          decoration: const BoxDecoration(
                            gradient: LinearGradient(colors:
                              [
                                Color(0xff4B75C9),
                                Color(0xc9D16DDA),
                                Color(0xffFFBB66),
                                Color(0xffFF8E00)
                              ]
                            )
                          ),
                          padding: const EdgeInsets.only(
                              top: 10, right: 10, bottom: 10),
                          child: Row(
                            children: [
                              SizedBox(
                                height: 100,
                                width: 100,
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
                              const SizedBox(width: 10),
                              Expanded(
                                child: Column(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    Expanded(
                                      child: Row(
                                        children: [
                                          const SizedBox(
                                            width: 90,
                                            child: Text(
                                              "T   I  T  L  E",
                                              style: TextStyle(
                                                  fontWeight: FontWeight.bold),
                                            ),
                                          ),
                                          Expanded(
                                            child: Text(
                                              timeline.title,
                                              maxLines: 1,
                                              overflow: TextOverflow.ellipsis,
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
                                                  fontWeight: FontWeight.bold),
                                            ),
                                          ),
                                          Expanded(
                                            child: Text(timeline.nickname),
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
                                                    fontWeight:
                                                        FontWeight.bold)),
                                          ),
                                          Expanded(
                                            child: Text(
                                                '${timeline.createTime} ~ ${timeline.finishTime}'),
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
                      )
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
