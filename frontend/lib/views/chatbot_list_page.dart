
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../view_models/chatbot_detail_view_model.dart';
import '../view_models/chatbot_list_view_model.dart';
import 'chatbot_detail_page.dart';

class ChatbotListPage extends StatelessWidget {

  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return Scaffold(
        body: Consumer<ChatbotListViewModel>(
          builder: (_, viewModel, __) {
            return ListView.builder(
              itemCount: viewModel.chatlist.length,
                itemBuilder: (BuildContext context, index) {
                  return GestureDetector(
                    onTap: () {
                        Navigator.push(context,
                          MaterialPageRoute(
                            builder: (context) =>
                                ChangeNotifierProvider<ChatbotViewModel>(
                                  create: (_) => ChatbotViewModel(context,viewModel.chatlist[index]["chatId"]),
                                  child: ChatbotPage(),
                                ),
                          ),);
                    },
                    child: ListTile(
                      leading: ClipRRect(
                        borderRadius: BorderRadius.circular(40),
                        child: Image.network("https://yt3.googleusercontent.com/ytc/AGIKgqM8zh66fZqGKeTkopHaU9GM4zvyuFnQhXThr37u=s900-c-k-c0x00ffffff-no-rj"),
                      ),
                      title: Text(viewModel.chatlist[index]["title"]),
                      subtitle: Text(viewModel.chatlist[index]["sub"]),
                    ),
                  );
                }
            );
          },

        ),
      );
  }

}