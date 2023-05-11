
import 'package:card_swiper/card_swiper.dart';
import 'package:flutter/material.dart';
import 'package:infinite_scroll_pagination/infinite_scroll_pagination.dart';
import 'package:moyeo/views/timeline_list_item_main.dart';
import 'package:provider/provider.dart';

import '../models/Timeline.dart';
import '../module/gradient_circular_indicator.dart';
import '../view_models/app_view_model.dart';

class TimelineListPageMain extends StatelessWidget {
  final PagingController<int, Timeline> pagingController;

  const TimelineListPageMain({super.key, required this.pagingController});

  @override
  Widget build(BuildContext context) => Consumer<AppViewModel>(
        builder: (_, appViewModel, __) {
          return RefreshIndicator(
            onRefresh: () async {
              pagingController.refresh();
            },
            // UI 바꾸기
            // child:Swiper(
            //   itemCount: pagingController.itemList?.length ?? 0,
            //   layout: SwiperLayout.STACK,
            //   itemWidth: MediaQuery.of(context).size.width*(0.8),
            //   itemBuilder: (BuildContext context, int index) {
            //   final timeline = pagingController.itemList![index];
            //     return TimelineListItemMain(
            //       key: Key(timeline.timelineId.toString()),
            //       timeline: timeline,
            //     );
            // },
            // ),
            child: PagedListView<int, Timeline>(
              shrinkWrap: true,
              physics: const AlwaysScrollableScrollPhysics(),
              scrollDirection: Axis.horizontal,
              pagingController: pagingController,
              builderDelegate: PagedChildBuilderDelegate<Timeline>(
                firstPageProgressIndicatorBuilder: (_) => Transform.scale(
                  scale: 0.2,
                  child: const SizedBox(
                    height: 400,
                    child: GradientCircularProgressIndicator()
                  ),
                ),
                noItemsFoundIndicatorBuilder: (context) => SizedBox(
                  width: MediaQuery.of(context).size.width*(0.5),
                  height: MediaQuery.of(context).size.height*(0.6),
                  child: Center(
                      child: Column(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: const [
                      Icon(
                        Icons.airplane_ticket_outlined,
                        color: Colors.grey,
                        size: 80,
                      ),
                      Text(
                        "다님과 함께 여행을 시작해볼까요?",
                        style: TextStyle(
                          color: Colors.grey,
                        ),
                      ),
                    ],
                  )),
                ),
                itemBuilder: (context, item, index) => TimelineListItemMain(
                  key: Key(item.timelineId.toString()),
                  timeline: item,
                ),
              )
            ));
        },
      );
}
