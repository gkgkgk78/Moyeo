import 'package:chat_bubbles/bubbles/bubble_special_one.dart';
import 'package:flutter/material.dart';
import 'package:logger/logger.dart';
import 'package:moyeo/view_models/chatbot_detail_view_model.dart';
import 'package:provider/provider.dart';


class Question extends StatelessWidget {
  int index;
  Question({required this.index});

  @override
  Widget build(BuildContext context) {
    return Consumer<ChatbotViewModel>(
      builder: (_, chatbotViewModel, __) {
        if (chatbotViewModel.isAnswered == false) {
          if (chatbotViewModel.isTravel != -1) {
            // 첫번째 질문
            if (chatbotViewModel.messages[index][0] == "안녕하세요! 여봇입니다.\n 무엇을 도와드릴까요?") {
              return Row(
                mainAxisAlignment:
                MainAxisAlignment.spaceEvenly,
                children: [
                  Container(
                    alignment: Alignment.center,
                    height: 24,
                    width: 120,
                    decoration: BoxDecoration(
                        color: Colors.amberAccent,
                        borderRadius:
                        BorderRadius.circular(10)),
                    child: Material(
                      color: Colors.transparent,
                      child: InkWell(
                        borderRadius:
                        BorderRadius.circular(10),
                        onTap: () {chatbotViewModel.selectActivity(context);},
                        splashColor: Colors.grey,
                        child: Container(
                          alignment: Alignment.center,
                          height: 24,
                          child: const Text('놀러갈 곳 추천"해줘"'),
                        ),
                      ),
                    ),
                  ),
                  Container(
                    alignment: Alignment.center,
                    height: 24,
                    width: 100,
                    decoration: BoxDecoration(
                        color: Colors.amberAccent,
                        borderRadius:
                        BorderRadius.circular(10)),
                    child: Material(
                      color: Colors.transparent,
                      child: InkWell(
                        borderRadius:
                        BorderRadius.circular(10),
                        onTap: () {chatbotViewModel.selectRestaurant(context);},
                        splashColor: Colors.grey,
                        child: Container(
                          alignment: Alignment.center,
                          height: 24,
                          child: const Text('식당 추천"해줘"'),
                        ),
                      ),
                    ),
                  )
                ],
              );
            } else {
              return const SizedBox.shrink();
            }
            // 여행 중이 아닐 때
          } else {
            if (chatbotViewModel.messages[index][0] == "안녕하세요! 여봇입니다. \n 가시고 싶은 곳은 정하셨나요?") {
              return Row(
                mainAxisAlignment:
                MainAxisAlignment.spaceEvenly,
                children: [
                  Container(
                    alignment: Alignment.center,
                    height: 24,
                    width: 40,
                    decoration: BoxDecoration(
                        color: Colors.amberAccent,
                        borderRadius:
                        BorderRadius.circular(10)),
                    child: Material(
                      color: Colors.transparent,
                      child: InkWell(
                        borderRadius:
                        BorderRadius.circular(10),
                        onTap: () {chatbotViewModel.userHaveDestination();},
                        splashColor: Colors.grey,
                        child: Container(
                          alignment: Alignment.center,
                          height: 24,
                          child: const Text('오냐'),
                        ),
                      ),
                    ),
                  ),
                  Container(
                    alignment: Alignment.center,
                    height: 24,
                    width: 40,
                    decoration: BoxDecoration(
                        color: Colors.amberAccent,
                        borderRadius:
                        BorderRadius.circular(10)),
                    child: Material(
                      color: Colors.transparent,
                      child: InkWell(
                        borderRadius:
                        BorderRadius.circular(10),
                        onTap: () {chatbotViewModel.userHaveNoDestination();},
                        splashColor: Colors.grey,
                        child: Container(
                          alignment: Alignment.center,
                          height: 24,
                          child: const Text('아니?'),
                        ),
                      ),
                    ),
                  )
                ],
              );
            } else {
              return const SizedBox.shrink();
            }
          }
        } else {
          return const SizedBox.shrink();
        }
    }
    );

  }
}
