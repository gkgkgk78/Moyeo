import 'package:flutter/material.dart';
import 'package:moyeo/view_models/message_list_view_model.dart';
import 'package:moyeo/views/push_alarm_page.dart';
import 'package:provider/provider.dart';

import '../view_models/app_view_model.dart';
import '../view_models/chatbot_detail_view_model.dart';
import 'chatbot_detail_page.dart';

class MessageListPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return Consumer<AppViewModel>(
      builder: (_, appViewModel, __) {
        return Consumer<MessageListViewModel>(
          builder: (_, viewModel, __) {
            return DefaultTabController(
              initialIndex: 0,
              length: 2,
              child: Scaffold(
                appBar: const TabBar(
                  labelColor: Colors.white,
                  unselectedLabelColor: Colors.grey,
                  indicator: ShapeDecoration(
                    shape: UnderlineInputBorder(
                        borderSide: BorderSide(
                            color: Colors.transparent,
                            width: 0,
                            style: BorderStyle.solid)),
                    gradient: LinearGradient(
                      colors: [
                        Colors.orangeAccent,
                        Colors.purpleAccent,
                        Colors.pink
                      ],
                    ),
                  ),
                  indicatorWeight: 1,
                  tabs: [
                    Center(
                      child: Text("여봇의 오지랖"),
                    ),
                    Center(
                      child: Text("인기인의 증거"),
                    ),
                  ],
                ),
                body: TabBarView(
                  children: [
                    ListView.builder(
                      itemCount: viewModel.yeobotList.length,
                      itemBuilder: (BuildContext context, index) {
                        return GestureDetector(
                          onTap: () {
                            appViewModel.changeTitle("여봇과의 추억");
                            Navigator.push(
                              context,
                              MaterialPageRoute(
                                builder: (context) => MultiProvider(
                                  providers: [
                                    ChangeNotifierProvider<ChatbotViewModel>(
                                      create: (_) => ChatbotViewModel(
                                        context,
                                        isTravel:
                                            appViewModel.userInfo.timeLineId,
                                      ),
                                    ),
                                  ],
                                  child: const ChatbotPage(),
                                ),
                              ),
                            );
                          },
                          child: ListTile(
                            leading: ClipRRect(
                              borderRadius: BorderRadius.circular(40),
                              child: Image.network(
                                  "https://yt3.googleusercontent.com/ytc/AGIKgqM8zh66fZqGKeTkopHaU9GM4zvyuFnQhXThr37u=s900-c-k-c0x00ffffff-no-rj"),
                            ),
                            title: Text(viewModel.yeobotList[index]["message"]),
                          ),
                        );
                      },
                    ),
                    ListView.builder(
                      itemCount: viewModel.pushList.length,
                      itemBuilder: (BuildContext context, index) {
                        return GestureDetector(
                          onTap: () {
                            appViewModel.changeTitle("왔던 알림");
                            Navigator.push(
                              context,
                              MaterialPageRoute(
                                builder: (context) => PushAlarmPage(),
                              ),
                            );
                          },
                          child: ListTile(
                            leading: ClipRRect(
                              borderRadius: BorderRadius.circular(40),
                              child: Image.network(
                                  "https://yt3.googleusercontent.com/ytc/AGIKgqM8zh66fZqGKeTkopHaU9GM4zvyuFnQhXThr37u=s900-c-k-c0x00ffffff-no-rj"),
                            ),
                            title: Text(viewModel.pushList[index]["title"]),
                            subtitle: Text(viewModel.pushList[index]["sub"]),
                          ),
                        );
                      },
                    ),
                  ],
                ),
              ),
            );
          }
        );
      },
    );
  }
}
