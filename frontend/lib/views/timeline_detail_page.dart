
import 'package:extended_image/extended_image.dart';
import 'package:flutter/material.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';
import 'package:timeline_tile/timeline_tile.dart';
import 'package:flutter_switch/flutter_switch.dart';


import '../views/moyeo_add_user.dart';
import '../views/post_list_item.dart';

import '../view_models/app_view_model.dart';
import '../view_models/timeline_detail_view_model.dart';

import '../utils/black.dart';

var logger = Logger();

class TimelineDetailPage extends StatelessWidget {
  const TimelineDetailPage({super.key});

  @override
  Widget build(BuildContext context) {
    final appViewModel = Provider.of<AppViewModel>(context);
    return Theme(
        data: ThemeData(
          fontFamily:"GangwonAll",
          primarySwatch: CustomColors.black,
        ),
      child:Scaffold(
        body: Consumer<TimelineDetailViewModel>(
          builder: (context, viewModel, _) => SingleChildScrollView(
            child: Column(
              children: [
                viewModel.isMine
                    ? Row(
                        children: [
                          viewModel.nowMoyeo && viewModel.timelineDetails.length > 0
                              ? InkWell(
                                  onTap: (){
                                    Navigator.push(
                                        context,
                                        MaterialPageRoute(
                                            builder: (context) => MoyeoAddUser()
                                        )
                                    );
                                  },
                                  child: Container(
                                      width: 130,
                                      margin: const EdgeInsets.only(left: 20) ,
                                      padding: const EdgeInsets.only(left: 10, right: 10, top:5, bottom: 5),
                                      decoration: BoxDecoration(
                                          boxShadow: [
                                            BoxShadow(
                                              color: Colors.grey.withOpacity(0.5),
                                              blurRadius: 1.0,
                                              spreadRadius: 1.0,
                                              offset: const Offset(2,2),
                                            )
                                          ],
                                          borderRadius: BorderRadius.circular(10),
                                          gradient: LinearGradient(
                                              colors: <Color>[
                                                Colors.redAccent,
                                                Colors.orangeAccent,
                                              ]
                                          )
                                      ),
                                      child: Row(
                                          children:[
                                            Icon(
                                              Icons.group_add,
                                              color: Colors.white,
                                            ),
                                            Text(
                                              "  모여 시작하기",
                                              style: TextStyle(color: Colors.white),
                                            ),
                                          ]
                                      )
                                  ),
                                )
                              : Container(),
                          Expanded(child: Container()),
                          Padding(
                            padding: const EdgeInsets.only(right: 5),
                            child: Text(viewModel.isPublic ? '공개' : ' 비공개'),
                          ),
                          FlutterSwitch(
                              width: 50,
                              height: 30,
                              toggleSize: 20,
                              activeColor: Colors.orangeAccent.withOpacity(0.7),
                              activeIcon: Icon(Icons.share),
                              inactiveColor: Colors.grey.withOpacity(0.7),
                              inactiveIcon: Icon(Icons.cancel_sharp),
                              value: viewModel.isPublic,
                              onToggle: (_){
                                viewModel.changeIsPublic(context);
                              }),
                          IconButton(
                            padding: const EdgeInsets.only(right: 35.0, bottom: 0),
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
                    Container(
                      margin: const EdgeInsets.only(
                          top:10.0, left: 30.0, right: 30.0, bottom: 10.0
                      ),
                      padding: const EdgeInsets.all(10.0),
                      decoration: BoxDecoration(
                        // border: Border.all(width: 1, color: Colors.grey),
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(20),
                        boxShadow: [
                          BoxShadow(
                            color: Colors.grey.withOpacity(0.5),
                            spreadRadius: 1,
                            blurRadius: 1,
                            offset: Offset(2,2),
                          )
                        ],
                      ),
                      child: viewModel.isMine && viewModel.timelineDetails.length == 0
                          ? ListView.builder(
                          shrinkWrap: true,
                          padding: const EdgeInsets.only(left: 1),
                          physics: const ClampingScrollPhysics(),
                          itemCount: viewModel.timelineDetails.length+1,
                          itemBuilder: (BuildContext context, int idx){
                            return Container(
                              child: Column(
                                children: [
                                  Container(
                                    padding: const EdgeInsets.all(10),
                                    child: Text("등록된 포스트가 없습니다"),
                                  ),
                                  viewModel.nowMoyeo
                                  ? Container(
                                    padding: const EdgeInsets.all(10),
                                    child: Text("포스트를 등록하거나 모여를 시작해보세요"),
                                  )
                                  : Container(),
                                  viewModel.nowMoyeo
                                  ? InkWell(
                                    onTap: (){
                                      Navigator.push(
                                          context,
                                          MaterialPageRoute(
                                              builder: (context) => MoyeoAddUser()
                                          )
                                      );
                                    },
                                    child: Container(
                                       width: 130,
                                        margin: const EdgeInsets.only(left: 20) ,
                                        padding: const EdgeInsets.only(left: 10, right: 10, top:5, bottom: 5),
                                        decoration: BoxDecoration(
                                            boxShadow: [
                                              BoxShadow(
                                                color: Colors.grey.withOpacity(0.5),
                                                blurRadius: 1.0,
                                                spreadRadius: 1.0,
                                                offset: const Offset(2,2),
                                              )
                                            ],
                                            borderRadius: BorderRadius.circular(10),
                                            gradient: LinearGradient(
                                                colors: <Color>[
                                                  Colors.redAccent,
                                                  Colors.orangeAccent,
                                                ]
                                            )
                                        ),
                                        child: Row(
                                            children:[
                                              Icon(
                                                Icons.group_add,
                                                color: Colors.white,
                                              ),
                                              Text(
                                                "  모여 시작하기",
                                                style: TextStyle(color: Colors.white),
                                              ),
                                            ]
                                        )
                                    ),
                                  )
                                  : Container()
                                ],
                              ),
                            );
                          }
                      )
                          : ListView.builder(
                                shrinkWrap: true,
                                padding: const EdgeInsets.only(left: 1),
                                physics: const ClampingScrollPhysics(),
                                itemCount: viewModel.timelineDetails.length,
                                // Number of nations
                                itemBuilder: (BuildContext context, int timelineIndex) {
                                  return ExpansionTile(
                                    tilePadding: const EdgeInsets.only(left: 10),
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
                                      padding: const EdgeInsets.only(right: 10.0),
                                      child: Text(
                                        '${viewModel.timelineDetails[timelineIndex].startDate} '
                                            '~ ${viewModel.timelineDetails[timelineIndex].finishDate}',
                                        style:
                                            const TextStyle(color: Colors.grey, fontSize: 13),
                                      ),
                                    ),
                                    leading: SizedBox(
                                      width: 35,
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
                              )
                            ),
                            viewModel.isMine && !viewModel.isComplete
                                ? Row(
                                    mainAxisAlignment: MainAxisAlignment.end,
                                    children: [
                                      viewModel.timelineDetails.length > 0
                                          ? !viewModel.nowMoyeo
                                            ? Container(
                                              margin: EdgeInsets.only(right: 35, top:5),
                                              child:Row(
                                              children:[
                                                InkWell(
                                                  onTap: (){
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
                                                  child: Container(
                                                    margin: const EdgeInsets.only(right: 10),
                                                    padding: const EdgeInsets.only(left: 10, right: 10, top:5, bottom: 5),
                                                    decoration: BoxDecoration(
                                                        boxShadow: [
                                                          BoxShadow(
                                                            color: Colors.grey.withOpacity(0.5),
                                                            blurRadius: 1.0,
                                                            spreadRadius: 1.0,
                                                            offset: const Offset(2,2),
                                                          )
                                                        ],
                                                        borderRadius: BorderRadius.circular(10),
                                                        gradient: LinearGradient(
                                                            colors: <Color>[
                                                              Colors.redAccent,
                                                              Colors.orangeAccent,
                                                            ]
                                                        )
                                                    ),
                                                    child: Row(
                                                        children:[
                                                          Icon(
                                                            Icons.done,
                                                            color: Colors.white,
                                                          ),
                                                          Text(
                                                            " 여행 끝",
                                                            style: TextStyle(color: Colors.white),
                                                          ),
                                                        ]
                                                    )
                                                  ),
                                                ),
                                            ]
                                              )
                                            )
                                        // 포스트를 등록하고 모여 타임라인 시작하기
                                            : Container()
                                          : InkWell(
                                              onTap: (){
                                                viewModel.outMoyeo(
                                                    context,
                                                    appViewModel.userInfo.moyeoTimelineId
                                                );
                                              },
                                              child: Container(
                                                width: 115,
                                                margin: const EdgeInsets.only(top:5, bottom:5, left: 20, right: 20) ,
                                                padding: const EdgeInsets.only(left: 10, right: 10, top:5, bottom: 5),
                                                decoration: BoxDecoration(
                                                    boxShadow: [
                                                      BoxShadow(
                                                        color: Colors.grey.withOpacity(0.5),
                                                        blurRadius: 1.0,
                                                        spreadRadius: 1.0,
                                                        offset: const Offset(2,2),
                                                      )
                                                    ],
                                                    borderRadius: BorderRadius.circular(10),
                                                    gradient: LinearGradient(
                                                        colors: <Color>[
                                                          Colors.redAccent,
                                                          Colors.orangeAccent,
                                                        ]
                                                    )
                                                ),
                                                child: Row(
                                                    children:[
                                                      Icon(
                                                        Icons.group_remove,
                                                        color: Colors.white,
                                                      ),
                                                      Text(
                                                        "  모여 나가기",
                                                        style: TextStyle(color: Colors.white),
                                                      ),
                                                    ]
                                                )
                                            ),
                                        ),
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
      )
    );
  }
}
