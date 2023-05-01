
import 'package:flutter/material.dart';
import 'package:infinite_scroll_pagination/infinite_scroll_pagination.dart';
import 'package:moyeo/views/timeline_list_item.dart';
import 'package:provider/provider.dart';

import '../models/Timeline.dart';
import '../module/gradient_circular_indicator.dart';
import '../view_models/app_view_model.dart';

class TimelineListPage extends StatelessWidget {
  final PagingController<int, Timeline> pagingController;

  const TimelineListPage({super.key, required this.pagingController});

  @override
  Widget build(BuildContext context) => Consumer<AppViewModel>(
        builder: (_, appViewModel, __) {
          return RefreshIndicator(
            onRefresh: () async {
              pagingController.refresh();
            },
            child: PagedListView<int, Timeline>(
              shrinkWrap: true,
              physics: const AlwaysScrollableScrollPhysics(),
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
                  height: 300,
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
                itemBuilder: (context, item, index) => TimelineListItem(
                  key: Key(item.timelineId.toString()),
                  timeline: item,
                ),
              ),
            ),
          );
        },
      );
}
