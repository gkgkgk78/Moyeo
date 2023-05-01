
import 'package:extended_image/extended_image.dart';
import 'package:flutter/material.dart';
import 'package:logger/logger.dart';
import 'package:moyeo/views/post_list_item.dart';
import 'package:provider/provider.dart';
import 'package:timeline_tile/timeline_tile.dart';

import '../view_models/app_view_model.dart';
import '../view_models/timeline_detail_view_model.dart';

var logger = Logger();

class TimelineDetailPage extends StatelessWidget {
  const TimelineDetailPage({super.key});

  @override
  Widget build(BuildContext context) {
    final appViewModel = Provider.of<AppViewModel>(context);
    return Scaffold(
      body: Consumer<TimelineDetailViewModel>(
        builder: (context, viewModel, _) => SingleChildScrollView(
          child: Column(
            children: [
              viewModel.isMine
                  ? Row(
                      children: [
                        Expanded(child: Container()),
                        Padding(
                          padding: const EdgeInsets.only(right: 5),
                          child: Text(viewModel.isPublic ? '공개' : ' 비공개'),
                        ),
                        viewModel.showPublicIcon(),
                        Switch(
                            value: viewModel.isPublic,
                            onChanged: (_) {
                              viewModel.changeIsPublic(context);
                            }),
                        IconButton(
                          onPressed: () {
                            showDialog(
                              barrierDismissible: false,
                              context: context,
                              builder: (ctx) => AlertDialog(
                                title: const Text('타임라인 삭제'),
                                content: const Text('타임라인을  삭제하시겠습니까?'),
                                actions: [
                                  TextButton(
                                    onPressed: () {
                                      viewModel.deleteTimeline(context);
                                      appViewModel.userInfo.timeLineId = -1;
                                      appViewModel.userInfo.timelineNum--;
                                      appViewModel.changeTitle(
                                          appViewModel.userInfo.nickname);
                                      Navigator.pop(ctx);
                                      Navigator.popAndPushNamed(context, '/');
                                    },
                                    child: const Text(
                                      '삭제',
                                      style: TextStyle(color: Colors.red),
                                    ),
                                  ),
                                  TextButton(
                                    onPressed: () {
                                      Navigator.pop(ctx);
                                    },
                                    child: const Text('취소'),
                                  ),
                                ],
                              ),
                            );
                          },
                          icon: const Icon(
                            Icons.delete,
                            color: Colors.red,
                          ),
                        )
                      ],
                    )
                  : Container(),
              ListView.builder(
                shrinkWrap: true,
                padding: const EdgeInsets.only(left: 5),
                physics: const ClampingScrollPhysics(),
                itemCount: viewModel.timelineDetails.length,
                // Number of nations
                itemBuilder: (BuildContext context, int timelineIndex) {
                  return ExpansionTile(
                    tilePadding: const EdgeInsets.only(left: 5),
                    shape: const RoundedRectangleBorder(),
                    collapsedShape: const RoundedRectangleBorder(),
                    onExpansionChanged: (isExpand) {
                      viewModel.changeExpansions(timelineIndex, isExpand);
                    },
                    title: Text(
                      viewModel.timelineDetails[timelineIndex].nation,
                      maxLines: 1,
                      overflow: TextOverflow.clip,
                    ),
                    trailing: Padding(
                      padding: const EdgeInsets.only(right: 40.0),
                      child: Text(
                        '${viewModel.timelineDetails[timelineIndex].startDate} ~ ${viewModel.timelineDetails[timelineIndex].finishDate}',
                        style:
                            const TextStyle(color: Colors.grey, fontSize: 13),
                      ),
                    ),
                    leading: SizedBox(
                      width: 60,
                      height: 60,
                      child: TimelineTile(
                        alignment: TimelineAlign.manual,
                        isFirst: timelineIndex == 0,
                        isLast: (timelineIndex ==
                                viewModel.timelineDetails.length - 1) &&
                            (!viewModel
                                .timelineDetails[timelineIndex].isExpand),
                        lineXY: 0.1,
                        beforeLineStyle: const LineStyle(
                          color: Colors.grey,
                          thickness: 1,
                        ),
                        afterLineStyle: const LineStyle(
                          color: Colors.grey,
                          thickness: 1,
                        ),
                        indicatorStyle: IndicatorStyle(
                          width: 30,
                          height: 30,
                          color: Colors.grey,
                          indicator: ExtendedImage.network(
                            viewModel.timelineDetails[timelineIndex].flag,
                            fit: BoxFit.cover,
                            cache: true,
                            shape: BoxShape.circle,
                            borderRadius: BorderRadius.circular(30),
                          ),
                        ),
                      ),
                    ),
                    children: [
                      ListView.builder(
                        shrinkWrap: true,
                        padding: EdgeInsets.zero,
                        physics: const NeverScrollableScrollPhysics(),
                        itemCount: viewModel
                            .timelineDetails[timelineIndex].postList.length,
                        // Number of nations
                        itemBuilder: (BuildContext context, int postIndex) {
                          return PostListItem(
                            timelineIndex: timelineIndex,
                            postIndex: postIndex,
                          );
                        },
                      ),
                    ],
                  );
                },
              ),
              viewModel.isMine && !viewModel.isComplete
                  ? Row(
                      mainAxisAlignment: MainAxisAlignment.end,
                      children: [
                        viewModel.timelineDetails.length > 0
                            ? ElevatedButton(
                                onPressed: () {
                                  showDialog(
                                    barrierDismissible: false,
                                    context: context,
                                    builder: (context) => AlertDialog(
                                      title: const Text('여행 제목을 입력해 주세요'),
                                      content: TextField(
                                        onChanged: (String value) {
                                          viewModel.changeTitle(value);
                                        },
                                        decoration: const InputDecoration(
                                            labelText: '제목',
                                            hintText: '필수 입니다.'),
                                      ),
                                      actions: [
                                        TextButton(
                                          onPressed: () {
                                            if (viewModel.title != null) {
                                              appViewModel.userInfo.timeLineId =
                                                  -1;
                                              appViewModel
                                                  .changeTitleToFormer();
                                              appViewModel.changeTitle(
                                                viewModel.title!,
                                              );
                                              Navigator.pop(context);
                                              viewModel.endTimeline(
                                                appViewModel.myFeedNavigatorKey
                                                    .currentContext,
                                              );
                                            }
                                          },
                                          child: const Text(
                                            '여행 종료',
                                          ),
                                        ),
                                        TextButton(
                                          onPressed: () {
                                            viewModel.resetTitle();
                                            Navigator.pop(context);
                                          },
                                          child: const Text('취소'),
                                        ),
                                      ],
                                    ),
                                  );
                                },
                                child: const Text(
                                  '여행 종료',
                                  style: TextStyle(color: Colors.white),
                                ),
                              )
                            : Container(),
                      ],
                    )
                  : Container(),
              const SizedBox(
                height: 40,
              )
            ],
          ),
        ),
      ),
    );
  }
}
