
import 'package:extended_image/extended_image.dart';
import 'package:flutter/material.dart';
import 'package:logger/logger.dart';
import 'package:moyeo/view_models/post_view_model.dart';
import 'package:moyeo/views/post_list_item.dart';
import 'package:provider/provider.dart';
import 'package:timeline_tile/timeline_tile.dart';
import 'package:flutter_switch/flutter_switch.dart';

import '../view_models/app_view_model.dart';
import '../view_models/timeline_detail_view_model.dart';
import '../view_models/search_bar_view_model.dart';
import '../view_models/search_result_view_model.dart';
import '../view_models/user_search_bar_view_model.dart';
import '../views/user_search_bar_view.dart';

import 'package:moyeo/utils/black.dart';

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
                          Expanded(child: Container()),
                          Padding(
                            padding: const EdgeInsets.only(right: 5),
                            child: Text(viewModel.isPublic ? '공개' : ' 비공개'),
                          ),
                          // viewModel.showPublicIcon(),
                          // Switch(
                          //     value: viewModel.isPublic,
                          //     onChanged: (_) {
                          //       viewModel.changeIsPublic(context);
                          //     }),
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
                      child: ListView.builder(
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
                                                          ])
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
                                            InkWell(
                                              onTap: (){
                                                showDialog(
                                                    barrierDismissible: false,
                                                    context: context,
                                                    builder: (context) => AlertDialog(
                                                      title: Container(
                                                          margin: const EdgeInsets.only(top: 0),
                                                          padding: const EdgeInsets.only(top:0),
                                                          child: Row(
                                                            children: [
                                                              const Text("동행 추가 하기"),
                                                              Expanded(child:Container()),
                                                              IconButton(
                                                                  onPressed: () {
                                                                    Navigator.pop(context);
                                                                  },
                                                                  icon: const Icon(Icons.close, color: Colors.red)
                                                              )
                                                            ],
                                                          )
                                                      ),
                                                      content:Column(
                                                        children:[
                                                          Container(
                                                              padding:const EdgeInsets.all(10.0),
                                                              child: const Text("함께 여행할 동행을 찾아 보세요."),
                                                          ),
                                                          Container(
                                                            color: Colors.white,
                                                            height: 45,
                                                            child:ChangeNotifierProvider<SearchBarViewModel>(
                                                              create: (_) => SearchBarViewModel(isMyFeed: false),
                                                              child: const UserSearchBar(),
                                                            )
                                                          ),
                                                          ChangeNotifierProvider<SelectedUsersProvider>(
                                                              create: (_)=> SelectedUsersProvider(),
                                                              child: Consumer<SelectedUsersProvider>(
                                                                builder: (context, provider, child) {
                                                                  final selectedUsers = provider.selectedUsers;
                                                                  return Column(
                                                                      children:[
                                                                        ListView.builder(
                                                                          itemCount: selectedUsers.length,
                                                                          itemBuilder: (context, index) {
                                                                          final user = selectedUsers[index];
                                                                            return ListTile(
                                                                                leading: CircleAvatar(
                                                                                  backgroundImage: NetworkImage(
                                                                                      user.profileImageUrl
                                                                                  ),
                                                                                ),
                                                                                title: Text(
                                                                                    user.nickname
                                                                                ),
                                                                                trailing: IconButton(
                                                                                  icon: Icon(
                                                                                      Icons.remove_circle),
                                                                                  onPressed: () {
                                                                                    provider.removeUser(user);
                                                                                  },
                                                                                )
                                                                            );
                                                                        },
                                                                      ),
                                                                      selectedUsers.length != 0
                                                                      ? InkWell(
                                                                          onTap:(){
                                                                            viewModel.addMoyeoUser(context, selectedUsers);
                                                                          } ,
                                                                        child: Container(
                                                                          child: Text("모여 시작"),
                                                                        ),
                                                                        )
                                                                     : Container()     
                                                                    ]
                                                                  );
                                                                }
                                                              )
                                                            ),

                                                          ]
                                                      )
                                                    )
                                                );
                                              },
                                              child: Container(
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
                                                    "  모여",
                                                    style: TextStyle(color: Colors.white),
                                                  ),
                                                ]
                                                )
                                              ),
                                            )
                                          ]
                                            )
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
      )
    );
  }
}
