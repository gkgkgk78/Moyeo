import 'package:extended_image/extended_image.dart';
import 'package:flutter/material.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';
import 'package:timeline_tile/timeline_tile.dart';
import 'package:flutter_switch/flutter_switch.dart';

import '../utils/avatar_pile.dart';
import '../views/moyeo_add_user.dart';
import '../views/post_list_item.dart';

import '../view_models/app_view_model.dart';
import '../view_models/timeline_detail_view_model.dart';

import '../utils/black.dart';
import 'camera_screen.dart';

var logger = Logger();

class TimelineDetailPage extends StatelessWidget {
  const TimelineDetailPage({super.key});

  @override
  Widget build(BuildContext context) {
    final appViewModel = Provider.of<AppViewModel>(context);
    final MyWidth = MediaQuery.of(context).size.width;
    final MyHeight = MediaQuery.of(context).size.height;
    return Theme(
        data: ThemeData(
          fontFamily: "GangwonAll",
          primarySwatch: CustomColors.black,
        ),
        child: Scaffold(
          body: Consumer<TimelineDetailViewModel>(
              builder: (context, viewModel, _) {
                viewModel.goBottom();
            return SingleChildScrollView(
              controller: viewModel.scrollController,
              child: Column(
                children: [
                  viewModel.isMine
                      ? Row(
                          children: [
                            viewModel.nowMoyeo // 모여 중이고 여행끝이 아닐때 때 회원 추가
                                    &&
                                    !viewModel.isComplete
                                ? InkWell(
                                    onTap: () {
                                      // 유저 추가하기
                                      Navigator.push(
                                          context,
                                          MaterialPageRoute(
                                            builder: (context) => MoyeoAddUser(
                                                members: viewModel.members),
                                          ));
                                    },
                                    child: Container(
                                        width: MyWidth * (0.32),
                                        margin: EdgeInsets.only(
                                            left: MyWidth * (0.08)),
                                        padding: EdgeInsets.only(
                                            left: MyWidth * (0.03),
                                            right: MyWidth * (0.03),
                                            top: MyHeight * (0.006),
                                            bottom: MyHeight * (0.006)),
                                        decoration: BoxDecoration(
                                            boxShadow: [
                                              BoxShadow(
                                                color: Colors.grey
                                                    .withOpacity(0.5),
                                                blurRadius: 1.0,
                                                spreadRadius: 1.0,
                                                offset: const Offset(2, 2),
                                              )
                                            ],
                                            borderRadius:
                                                BorderRadius.circular(10),
                                            gradient: const LinearGradient(
                                                colors: <Color>[
                                                  Colors.redAccent,
                                                  Colors.orangeAccent,
                                                ])),
                                        child: Row(children: [
                                          Icon(
                                            Icons.group_add,
                                            color: Colors.white,
                                          ),
                                          Text(
                                            "  모여 초대하기",
                                            style: TextStyle(
                                                color: Colors.white,
                                                fontSize: MyHeight * (0.017)),
                                          ),
                                        ])),
                                  )
                                : Container(),
                            !viewModel.nowMoyeo &&
                                    !viewModel
                                        .isComplete // 모여중이 아닐경우 모여 시작 버튼 보임
                                ? InkWell(
                                    onTap: () async {
                                      // 모여 시작하기
                                      await viewModel.startMoyeo(context);
                                      await viewModel
                                          .loadTimelineDetails(context);
                                    },
                                    child: Container(
                                        width: MyWidth * (0.32),
                                        margin: EdgeInsets.only(
                                            left: MyWidth * (0.08)),
                                        padding: EdgeInsets.only(
                                            left: MyWidth * (0.03),
                                            right: MyWidth * (0.03),
                                            top: MyHeight * (0.006),
                                            bottom: MyHeight * (0.006)),
                                        decoration: BoxDecoration(
                                            boxShadow: [
                                              BoxShadow(
                                                color: Colors.grey
                                                    .withOpacity(0.5),
                                                blurRadius: 1.0,
                                                spreadRadius: 1.0,
                                                offset: const Offset(2, 2),
                                              )
                                            ],
                                            borderRadius:
                                                BorderRadius.circular(10),
                                            gradient: const LinearGradient(
                                                colors: <Color>[
                                                  Colors.redAccent,
                                                  Colors.orangeAccent,
                                                ])),
                                        child: Row(children: [
                                          Icon(
                                            Icons.group_add,
                                            color: Colors.white,
                                          ),
                                          Text(
                                            "  모여 시작하기",
                                            style: TextStyle(
                                                color: Colors.white,
                                                fontSize: MyHeight * (0.017)),
                                          ),
                                        ])),
                                  )
                                : Container(),
                            Expanded(child: Container()),
                            Padding(
                              padding: EdgeInsets.only(right: MyWidth * (0.02)),
                              child: Text(
                                viewModel.isPublic ? '공개' : ' 비공개',
                                style: TextStyle(fontSize: MyHeight * (0.017)),
                              ),
                            ),
                            FlutterSwitch(
                                width: MyWidth * (0.12),
                                height: MyHeight * (0.035),
                                toggleSize: MyHeight * (0.025),
                                activeColor:
                                    Colors.orangeAccent.withOpacity(0.7),
                                activeIcon: const Icon(Icons.share),
                                inactiveColor: Colors.grey.withOpacity(0.7),
                                inactiveIcon: const Icon(Icons.cancel_sharp),
                                value: viewModel.isPublic,
                                onToggle: (_) {
                                  viewModel.changeIsPublic(context);
                                }),
                            IconButton(
                              padding: EdgeInsets.only(
                                  right: MyWidth * (0.07), bottom: 0),
                              onPressed: () {
                                showDialog(
                                    barrierDismissible: false,
                                    context: context,
                                    builder: (ctx) =>
                                        // 모여 끝내기 전에 타임라인 삭제 금지
                                        appViewModel.userInfo.moyeoTimelineId ==
                                                -1
                                            ? AlertDialog(
                                                title: const Text('타임라인 삭제'),
                                                content: const Text(
                                                    '타임라인을  삭제하시겠습니까?'),
                                                actions: [
                                                  TextButton(
                                                    onPressed: () {
                                                      viewModel.deleteTimeline(
                                                          context);
                                                      appViewModel.userInfo
                                                          .timeLineId = -1;
                                                      appViewModel.userInfo
                                                          .timelineNum--;
                                                      appViewModel.changeTitle(
                                                          appViewModel.userInfo
                                                              .nickname);
                                                      Navigator.pop(ctx);
                                                      Navigator.popAndPushNamed(
                                                          context, '/');
                                                    },
                                                    child: const Text(
                                                      '삭제',
                                                      style: TextStyle(
                                                          color: Colors.red),
                                                    ),
                                                  ),
                                                  TextButton(
                                                    onPressed: () {
                                                      Navigator.pop(ctx);
                                                    },
                                                    child: const Text('취소'),
                                                  ),
                                                ],
                                              )
                                            : AlertDialog(
                                                title: const Text('타임라인 삭제'),
                                                content: const Text(
                                                    '모여 중엔 타임라인을 삭제할 수 없습니다.'),
                                                actions: [
                                                  TextButton(
                                                    onPressed: () {
                                                      Navigator.pop(ctx);
                                                    },
                                                    child: const Text(
                                                      '닫기',
                                                      style: TextStyle(
                                                          color: Colors.red),
                                                    ),
                                                  ),
                                                ],
                                              ));
                              },
                              icon: const Icon(
                                Icons.delete,
                                color: Colors.red,
                              ),
                            )
                          ],
                        )
                      : Container(),
                  viewModel.nowMoyeo &&
                          !viewModel.isComplete &&
                          viewModel.nowMoyeo
                      ? Container(
                          child: AvatarPile(
                          avatars: List.generate(
                            viewModel.members.length,
                            (index) {
                              var people = viewModel.members[index];
                              return CircleAvatar(
                                  radius: 15,
                                  backgroundImage: NetworkImage(
                                      people['profileImageUrl'].toString()));
                            },
                          ),
                          title: viewModel.members.length > 1
                              ? viewModel.members.length < 3
                                  ? '${viewModel.members[1]['nickname']}님 과 여행 중'
                                  : '${viewModel.members[1]['nickname']}님 외 '
                                      '${viewModel.members.length - 2}명 과 동행중'
                              : "아직 동행이 추가되지 않았습니다",
                          pileSize: MyWidth * (0.1),
                          avatarSize: MyWidth * (0.1),
                          avatarOverlap: 0.5,
                        ))
                      : Container(),
                  Container(
                      margin: const EdgeInsets.only(
                          top: 10.0, left: 30.0, right: 30.0, bottom: 10.0),
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
                            offset: const Offset(2, 2),
                          )
                        ],
                      ),
                      child: viewModel.isMine &&
                              viewModel.timelineDetails.length == 0
                          ? ListView.builder(
                              shrinkWrap: true,
                              padding: const EdgeInsets.only(left: 1),
                              physics: const ClampingScrollPhysics(),
                              itemCount: viewModel.timelineDetails.length + 1,
                              itemBuilder: (BuildContext context, int idx) {
                                return Column(
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceAround,
                                  children: [
                                    Container(
                                      padding: const EdgeInsets.all(10),
                                      child: const Text("등록된 포스트가 없습니다"),
                                    ),
                                    !viewModel.nowMoyeo
                                        ? Container(
                                            // padding: const EdgeInsets.all(10),
                                            child: Column(
                                                mainAxisAlignment:
                                                    MainAxisAlignment
                                                        .spaceBetween,
                                                children: [
                                                Container(
                                                    padding:
                                                        const EdgeInsets.all(
                                                            10),
                                                    child: const Text(
                                                        "포스트를 등록하거나 모여를 시작해보세요.")),
                                                Container(
                                                    padding: EdgeInsets.all(10),
                                                    child: const Text(
                                                      "가운데 동그란 버튼을 눌러 포스트를 등록해보세요.",
                                                    )),
                                              ]))
                                        : viewModel.members.length > 1
                                            ? Container(
                                                padding:
                                                    const EdgeInsets.all(10),
                                                child: Column(children: [
                                                  const Text(
                                                      "가운데 버튼을 눌러 포스트를 등록해주세요"),
                                                ]))
                                            : Container(
                                                padding: EdgeInsets.all(10),
                                                child:
                                                    const Text("동행자를 추가해 주세요."),
                                              )
                                  ],
                                );
                              })
                          : ListView.builder(
                              shrinkWrap: true,
                              padding: const EdgeInsets.only(left: 1),
                              physics: const ClampingScrollPhysics(),
                              itemCount: viewModel.timelineDetails.length,
                              // Number of nations
                              itemBuilder:
                                  (BuildContext context, int timelineIndex) {
                                return ExpansionTile(
                                  initiallyExpanded: true,
                                  tilePadding: const EdgeInsets.only(left: 10),
                                  shape: const RoundedRectangleBorder(),
                                  collapsedShape:
                                      const RoundedRectangleBorder(),
                                  onExpansionChanged: (isExpand) {
                                    viewModel.changeExpansions(
                                        timelineIndex, isExpand);
                                  },
                                  title: Text(
                                    viewModel
                                        .timelineDetails[timelineIndex].nation,
                                    maxLines: 1,
                                    overflow: TextOverflow.clip,
                                  ),
                                  trailing: Padding(
                                    padding: const EdgeInsets.only(right: 10.0),
                                    child: Text(
                                      '${viewModel.timelineDetails[timelineIndex].startDate} '
                                      '~ ${viewModel.timelineDetails[timelineIndex].finishDate}',
                                      style:
                                          // 13 포인트가 대략 0.015
                                          TextStyle(
                                              color: Colors.grey,
                                              fontSize: MyHeight * (0.015)),
                                    ),
                                  ),
                                  leading: SizedBox(
                                    width: 35,
                                    height: 60,
                                    child: TimelineTile(
                                      alignment: TimelineAlign.manual,
                                      isFirst: timelineIndex == 0,
                                      isLast: (timelineIndex ==
                                              viewModel.timelineDetails.length -
                                                  1) &&
                                          (!viewModel
                                              .timelineDetails[timelineIndex]
                                              .isExpand),
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
                                          viewModel
                                              .timelineDetails[timelineIndex]
                                              .flag,
                                          fit: BoxFit.cover,
                                          cache: true,
                                          shape: BoxShape.circle,
                                          borderRadius:
                                              BorderRadius.circular(30),
                                        ),
                                      ),
                                    ),
                                  ),
                                  children: [
                                    ListView.builder(
                                      shrinkWrap: true,
                                      padding: EdgeInsets.zero,
                                      physics:
                                          const NeverScrollableScrollPhysics(),
                                      itemCount: viewModel
                                          .timelineDetails[timelineIndex]
                                          .postList
                                          .length,
                                      // Number of nations
                                      itemBuilder: (BuildContext context,
                                          int postIndex) {
                                        return PostListItem(
                                          timelineIndex: timelineIndex,
                                          postIndex: postIndex,
                                          lastIndex: viewModel
                                                  .timelineDetails[
                                                      timelineIndex]
                                                  .postList
                                                  .length -
                                              1,
                                        );
                                      },
                                    ),
                                  ],
                                );
                              },
                            )),
                  viewModel.isMine && !viewModel.isComplete
                      ? Row(
                          mainAxisAlignment: MainAxisAlignment.end,
                          children: [
                            viewModel.timelineDetails.length > 0 &&
                                    !viewModel.nowMoyeo
                                ? Container(
                                    margin: const EdgeInsets.only(
                                        right: 35, top: 5),
                                    child: Row(children: [
                                      InkWell(
                                        onTap: () {
                                          showDialog(
                                            barrierDismissible: false,
                                            context: context,
                                            builder: (context) => AlertDialog(
                                              title:
                                                  const Text('여행 제목을 입력해 주세요'),
                                              content: TextField(
                                                onChanged: (String value) {
                                                  viewModel.changeTitle(value);
                                                },
                                                decoration:
                                                    const InputDecoration(
                                                        labelText: '제목',
                                                        hintText: '필수 입니다.'),
                                              ),
                                              actions: [
                                                TextButton(
                                                  onPressed: () {
                                                    if (viewModel.title !=
                                                        null) {
                                                      appViewModel.userInfo
                                                          .timeLineId = -1;
                                                      appViewModel
                                                          .changeTitleToFormer();
                                                      appViewModel.changeTitle(
                                                        viewModel.title!,
                                                      );
                                                      Navigator.pop(context);
                                                      viewModel.endTimeline(
                                                        appViewModel
                                                            .myFeedNavigatorKey
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
                                            margin: const EdgeInsets.only(
                                                right: 10),
                                            padding: const EdgeInsets.only(
                                                left: 10,
                                                right: 10,
                                                top: 5,
                                                bottom: 5),
                                            decoration: BoxDecoration(
                                                boxShadow: [
                                                  BoxShadow(
                                                    color: Colors.grey
                                                        .withOpacity(0.5),
                                                    blurRadius: 1.0,
                                                    spreadRadius: 1.0,
                                                    offset: const Offset(2, 2),
                                                  )
                                                ],
                                                borderRadius:
                                                    BorderRadius.circular(10),
                                                gradient: const LinearGradient(
                                                    colors: <Color>[
                                                      Colors.redAccent,
                                                      Colors.orangeAccent,
                                                    ])),
                                            child: Row(children: const [
                                              Icon(
                                                Icons.done,
                                                color: Colors.white,
                                              ),
                                              Text(
                                                " 여행 끝",
                                                style: TextStyle(
                                                    color: Colors.white),
                                              ),
                                            ])),
                                      ),
                                    ]))
                                // 포스트를 등록하고 모여 타임라인 시작하기
                                : Container(),
                            viewModel.nowMoyeo
                                ? InkWell(
                                    onTap: () async {
                                      await viewModel.outMoyeo(
                                          context,
                                          appViewModel.userInfo.userUid,
                                          appViewModel
                                              .userInfo.moyeoTimelineId);
                                      await viewModel
                                          .loadTimelineDetails(context);
                                    },
                                    child: Container(
                                        width: 115,
                                        margin: EdgeInsets.only(
                                            top: 5,
                                            bottom: 5,
                                            left: 20,
                                            right: MyWidth * (0.08)),
                                        padding: const EdgeInsets.only(
                                            left: 10,
                                            right: 10,
                                            top: 5,
                                            bottom: 5),
                                        decoration: BoxDecoration(
                                            boxShadow: [
                                              BoxShadow(
                                                color: Colors.grey
                                                    .withOpacity(0.5),
                                                blurRadius: 1.0,
                                                spreadRadius: 1.0,
                                                offset: const Offset(2, 2),
                                              )
                                            ],
                                            borderRadius:
                                                BorderRadius.circular(10),
                                            gradient: const LinearGradient(
                                                colors: <Color>[
                                                  Colors.redAccent,
                                                  Colors.orangeAccent,
                                                ])),
                                        child: Row(children: const [
                                          Icon(
                                            Icons.group_remove,
                                            color: Colors.white,
                                          ),
                                          Text(
                                            "  모여 나가기",
                                            style:
                                                TextStyle(color: Colors.white),
                                          ),
                                        ])),
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
            );
          }),
        ));
  }
}
