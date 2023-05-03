import 'package:chat_bubbles/bubbles/bubble_special_one.dart';
import 'package:flutter/material.dart';
import 'package:logger/logger.dart';
import 'package:mongo_dart/mongo_dart.dart';
import 'package:provider/provider.dart';

import '../view_models/chatbot_detail_view_model.dart';
import 'chatbot_question.dart';

var logger = Logger();

class ChatbotPage extends StatelessWidget {
  const ChatbotPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Consumer<ChatbotViewModel>(builder: (_, viewModel, __) {
        return LayoutBuilder(builder: (_, BoxConstraints constraints) {
          return GestureDetector(
            onTap: () {
              viewModel.unFocus();
            },
            child: Column(
              children: [
                SizedBox(
                  height: viewModel.boxHeight(constraints),
                  child: ListView.builder(
                    controller: viewModel.scrollController,
                    itemCount: viewModel.messages.length,
                    itemBuilder: (BuildContext context, int index) {
                      return Column(
                        crossAxisAlignment: viewModel.getAlignment(index),
                        children: [
                          Container(
                              margin: const EdgeInsets.all(10),
                              alignment: Alignment.bottomRight,
                              width: 30,
                              height: 30,
                              child: viewModel.profileImage(index)),
                          BubbleSpecialOne(
                            text: viewModel.messages[index][0],
                            isSender: viewModel.sender(index),
                            color: viewModel.bubbleColor(index),
                          ),
                          Question(index: index,),
                        ],
                      );
                    },
                  ),
                ),
                Column(
                  children: [
                    Row(
                      children: [
                        Expanded(
                            child: TextFormField(
                              enabled: viewModel.ableTextField,
                          controller: viewModel.textEditingController,
                          onChanged: (text) {
                            viewModel.changeInputText(text);
                          },
                          onFieldSubmitted: (_) {
                            viewModel.testsubmit(viewModel.inputText);
                          },
                          focusNode: viewModel.chatbotFocus,
                          decoration: const InputDecoration(
                            fillColor: Colors.white,
                            filled: true,
                          ),
                        )),
                        GestureDetector(
                          onTap: () {
                            viewModel.testsubmit(viewModel.inputText);
                          },
                          child: Container(
                            decoration: const BoxDecoration(
                                borderRadius:
                                    BorderRadius.all(Radius.circular(10)),
                                gradient: LinearGradient(
                                    begin: Alignment.bottomLeft,
                                    end: Alignment.topRight,
                                    colors: [
                                      Colors.blueAccent,
                                      Colors.purpleAccent,
                                      Colors.orangeAccent
                                    ])),
                            width: 40,
                            height: 40,
                            child: const Icon(
                              Icons.east_rounded,
                              color: Colors.white,
                            ),
                          ),
                        )
                      ],
                    ),
                    !viewModel.chatbotFocus.hasFocus
                        ? Container(
                            height: 40,
                            color: Color(0xff7f7f7),
                          )
                        : SizedBox.shrink()
                  ],
                ),
              ],
            ),
          );
        });
      }),
    );
  }
}
