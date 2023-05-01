
import 'package:flutter/material.dart';
import 'package:moyeo/views/post_detail.dart';
import 'package:provider/provider.dart';
import 'package:timeline_tile/timeline_tile.dart';

import '../view_models/post_view_model.dart';
import '../view_models/timeline_detail_view_model.dart';

class PostListItem extends StatelessWidget {
  final timelineIndex;
  final postIndex;

  const PostListItem(
      {super.key, required this.timelineIndex, required this.postIndex});

  @override
  Widget build(BuildContext context) {
    return Consumer<TimelineDetailViewModel>(builder: (_, viewModel, __) {
      return ExpansionTile(
        shape: const RoundedRectangleBorder(),
        collapsedShape: const RoundedRectangleBorder(),
        tilePadding: const EdgeInsets.only(left: 5),
        onExpansionChanged: (isExpand) {
          viewModel.changePostExpansion(
            context,
            timelineIndex,
            postIndex,
            isExpand,
          );
        },
        title: Text(
          viewModel.timelineDetails[timelineIndex].postList[postIndex].address,
          maxLines: 1,
          overflow: TextOverflow.ellipsis,
        ),
        leading: SizedBox(
          width: 40,
          height: 60,
          child: TimelineTile(
            isLast: (timelineIndex == viewModel.timelineDetails.length - 1) &&
                (postIndex ==
                    viewModel.timelineDetails[timelineIndex].postList.length -
                        1) &&
                (!viewModel.timelineDetails[timelineIndex].postList[postIndex]
                    .isExpand),
            indicatorStyle: const IndicatorStyle(
                width: 20,
                color: Colors.grey,
                padding: EdgeInsets.only(left: 10)),
            beforeLineStyle:  const LineStyle(
              color: Colors.grey,
              thickness: 1,
            ),
            afterLineStyle:  const LineStyle(
              color: Colors.grey,
              thickness: 1,
            ),
            endChild: Container(
              padding: const EdgeInsets.all(8),
            ),
          ),
        ),
        children: [
          ChangeNotifierProvider(
            create: (_) => PostViewModel(
                viewModel.timelineDetails[timelineIndex].postList[postIndex],
                viewModel.isMine),
            child: PostDetail(
              key: Key(
                viewModel
                    .timelineDetails[timelineIndex].postList[postIndex].postId
                    .toString(),
              ),
            ),
          ),
        ],
      );
    });
  }
}
