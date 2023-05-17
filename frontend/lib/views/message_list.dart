import 'package:flutter/material.dart';
import 'package:moyeo/module/gradient_circular_indicator.dart';
import 'package:moyeo/view_models/message_list_view_model.dart';
import 'package:moyeo/views/push_alarm_page.dart';
import 'package:provider/provider.dart';

import '../view_models/app_view_model.dart';
import '../view_models/chatbot_detail_view_model.dart';

class MessageListPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Consumer<AppViewModel>(
      builder: (_, appViewModel, __) {
        return Consumer<MessageListViewModel>(builder: (_, viewModel, __) {
          return DefaultTabController(
            initialIndex: viewModel.initialIndex,
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
                  viewModel.isGetting == true
                  ? Transform.scale(
                    scaleX: 0.4,
                      scaleY: 0.26,
                      child: const GradientCircularProgressIndicator())
                  : viewModel.gptList.isEmpty
                      ? GestureDetector(
                        onTap: () {appViewModel.goYeobotPage();},
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          mainAxisSize: MainAxisSize.max,
                          children: const [
                            Icon(Icons.smart_toy_sharp),
                            Text(
                              "여봇에게서 온 답변이 없네요!\n여봇에게 질문을 해볼까요?",
                              textAlign: TextAlign.center,
                            )
                          ],
                        ),
                      )
                      : ListView.builder(
                          itemCount: viewModel.gptList.length,
                          itemBuilder: (BuildContext context, index) {
                            return Padding(
                              padding: const EdgeInsets.only(top: 2, bottom: 2),
                              child: GestureDetector(
                                onTap: () {},
                                child: Column(
                                  children: [
                                    ListTile(
                                      leading: ClipRRect(
                                        borderRadius: BorderRadius.circular(40),
                                        child: Image.asset(
                                            'assets/images/canton.png'),
                                      ),
                                      title: Text(
                                        viewModel.gptList[index].content,
                                        style: TextStyle(
                                            color: viewModel.checkColor(
                                                viewModel.gptList[index])),
                                      ),
                                    ),
                                    const Divider(
                                      color: Colors.orangeAccent,
                                    )
                                  ],
                                ),
                              ),
                            );
                          },
                        ),
                  viewModel.inviteList.isEmpty
                      ? GestureDetector(
                    onTap: () {
                      Navigator.of(context).pop();
                    },
                        child: Center(
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.center,
                              mainAxisSize: MainAxisSize.max,
                              children: const [
                                Icon(Icons.handshake_outlined),
                                Text("다른 사람과 여행을 함께 해 보세요!")
                              ],
                            ),
                          ),
                      )
                      : ListView.builder(
                          itemCount: viewModel.inviteList.length,
                          itemBuilder: (BuildContext context, index) {
                            return Padding(
                              padding: const EdgeInsets.only(top: 2, bottom: 5),
                              child: Column(
                                children: [
                                  ListTile(
                                    leading: ClipRRect(
                                      borderRadius: BorderRadius.circular(40),
                                      child: Image.asset(
                                          'assets/images/shakinghand.png'),
                                    ),
                                    title: Text(
                                      viewModel.inviteList[index].content,
                                      style: TextStyle(
                                          color: viewModel.checkColor(
                                              viewModel.gptList[index])),
                                    ),
                                  ),
                                  const Divider(
                                    color: Colors.redAccent,
                                  )
                                ],
                              ),
                            );
                          },
                        ),
                ],
              ),
            ),
          );
        });
      },
    );
  }
}
