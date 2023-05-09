
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:extended_image/extended_image.dart';
import 'package:moyeo/view_models/app_view_model.dart';
import 'package:moyeo/view_models/search_bar_view_model.dart';
import 'package:moyeo/view_models/user_search_bar_view_model.dart';
import 'package:moyeo/views/user_search_bar_view.dart';
import 'package:moyeo/views/moyeo_timeline.dart';
import 'package:provider/provider.dart';

class MoyeoAddUser extends StatelessWidget{
  const MoyeoAddUser({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context){
    return Consumer<AppViewModel>(
        builder: (_, appViewModel, __) {
          return ChangeNotifierProvider(
              create: (_) => SelectedUsersProvider(),
              builder:(context, _) {
                return Consumer<SelectedUsersProvider>(
                    builder: (context, selectedUsersProvider, _){
                      final selectedUser = selectedUsersProvider.selectedUsers;
                      return SingleChildScrollView(
                          child: Column(
                          children: [
                          Container(
                                margin: const EdgeInsets.only(left: 10, right: 10, top: 10),
                                child: ChangeNotifierProvider<SearchBarViewModel>(
                                  create: (_)=> SearchBarViewModel(isMyFeed: false),
                                  child: const UserSearchBar(),
                                ),
                              ),
                          GestureDetector(
                            behavior: HitTestBehavior.translucent,
                            onTap: () {
                              FocusScope.of(context).unfocus();
                            },
                            child: Container(
                              width: MediaQuery.of(context).size.width*(0.8),
                              height: MediaQuery.of(context).size.height*(0.6),
                                  decoration: BoxDecoration(
                                    border: Border.all(width: 1)
                                  ),
                                  child: selectedUser.length != 0
                                    ? ListView.builder(
                                        itemCount: selectedUser.length,
                                        itemBuilder: (BuildContext context, int index) {
                                          final user = selectedUser[index];
                                          return ListTile(
                                            leading: SizedBox(
                                              width: 45,
                                              height: 45,
                                              child: ClipRRect(
                                                borderRadius:BorderRadius.circular(30.0),
                                                child: ExtendedImage.network(
                                                  user.profileImageUrl,
                                                  fit: BoxFit.cover,
                                                  cache: true,
                                                ),
                                              ),
                                            ),
                                            title: Text(user.nickname),
                                            trailing: IconButton(
                                              onPressed: (){
                                                selectedUsersProvider.removeUser(user);
                                              },
                                              icon: const Icon(Icons.cancel, color: Colors.redAccent,),
                                            ),
                                          );
                                        },
                                      )
                                    : Container(child:Text("동행멤버를 추가해주세요")),
                            ),
                          ),
                          selectedUser.length != 0
                            ? Container(
                              padding: EdgeInsets.only(bottom: 30) ,
                              child: ElevatedButton(
                                onPressed: (){
                                  selectedUsersProvider.addMoyeoUser(context, selectedUser);
                                  Navigator.push(
                                    context,
                                    MaterialPageRoute(
                                        builder: (context) => MoyeoTimeline()
                                    )
                                  );
                                },
                                child: Text("동행시작"),
                              )
                            )
                            : Container()
                          ],
                      ));
                    }
                    );
                }
              );
              }
          );
        }
}