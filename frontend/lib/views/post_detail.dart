import 'package:animated_icon_button/animated_icon_button.dart';

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:timeline_tile/timeline_tile.dart';
import 'package:flutter_switch/flutter_switch.dart';

// import 'package:flutter_face_pile/flutter_face_pile.dart';
import '../utils/avatar_pile.dart';


import '../module/audio_player_view.dart';
import '../module/audio_player_view_model.dart';
import '../module/images_page_view.dart';
import '../view_models/app_view_model.dart';
import '../view_models/post_view_model.dart';

class PostDetail extends StatelessWidget {

  const PostDetail({super.key});

  @override
  Widget build(BuildContext context) {
    bool isSwitched = true;
    return Consumer<PostViewModel>(builder: (context, viewModel, _) {
      return Row(
        children: [
          SizedBox(
            width: 40,
            height: 500,
            child: TimelineTile(
              indicatorStyle: const IndicatorStyle(
                width: 0,
                padding: EdgeInsets.only(left: 10),
              ),
              beforeLineStyle:  const LineStyle(
                color: Colors.grey,
                thickness: 1,
              ),
              afterLineStyle:  const LineStyle(
                color: Colors.grey,
                thickness: 1,
              ),
              alignment: TimelineAlign.center,
            ),
          ),
          SizedBox(
            width: MediaQuery.of(context).size.width*(0.65),
            height: 500,
            child: Column(
              children: [
                Expanded(
                  child: GestureDetector(
                    onTap: () {
                      showDialog(
                        context: context,
                        builder: (context) {
                          return Dialog(
                            insetPadding: const EdgeInsets.all(20),
                            child: ImagesPageView(
                              listImageUrl: viewModel.post.photoList,
                              boxFit: BoxFit.contain,
                            ),
                          );
                        },
                      );
                    },
                    child:
                        ImagesPageView(listImageUrl: viewModel.post.photoList),
                  ),
                ),
                Consumer<AppViewModel>(builder: (_, appViewModel, __) {
                  return SizedBox(
                    height: 60,
                    child: Row(
                      crossAxisAlignment: CrossAxisAlignment.center,
                      children: [
                        AnimatedIconButton(
                          splashColor: Colors.transparent,
                          highlightColor: Colors.transparent,
                          initialIcon: viewModel.post.isFavorite ? 1 : 0,
                          onPressed: () {
                            viewModel.post.isMoyeo
                            // 모여 좋아요
                            ? viewModel.changeMoyeoFavorite(
                                context, appViewModel.userInfo.userUid)
                            // 일반 포스트 좋아요
                            : viewModel.changeIsFavorite(
                                context, appViewModel.userInfo.userUid);
                          },
                          icons: const <AnimatedIconItem>[
                            AnimatedIconItem(
                              icon: Icon(
                                Icons.favorite,
                                color: Colors.grey,
                              ),
                            ),
                            AnimatedIconItem(
                              icon: Icon(
                                Icons.favorite,
                                color: Colors.pinkAccent,
                              ),
                            ),
                          ],
                        ),
                        Text(
                            viewModel.post.favoriteCount.toString()
                        ),
                        Expanded(child: Container()),
                        // 삭제 버튼 구현 할 자리
                        // appViewModel.userInfo.moyeoTimelineId != -1
                        // ? IconButton(
                        //   onPressed: () {
                        //     showDialog(
                        //       barrierDismissible: false,
                        //       context: context,
                        //       builder: (ctx) => AlertDialog(
                        //         title: const Text('포스트 삭제'),
                        //         content: const Text('포스트를 삭제하시겠습니까?'),
                        //         actions: [
                        //           TextButton(
                        //             onPressed: () {
                        //               // 모여 포스트, 일반포스트 삭제
                        //               viewModel.post.isMoyeo
                        //               ? viewModel.deletePost(context, viewModel.post.postId)
                        //               : viewModel.deleteMoyeoPost(context, viewModel.post.postId);
                        //             },
                        //             child: const Text(
                        //               '삭제',
                        //               style: TextStyle(color: Colors.red),
                        //             ),
                        //           ),
                        //           TextButton(
                        //             onPressed: () {
                        //               Navigator.pop(ctx);
                        //             },
                        //             child: const Text('취소'),
                        //           ),
                        //         ],
                        //       ),
                        //     );
                        //   },
                        //   icon: const Icon(
                        //     Icons.delete,
                        //     color: Colors.red,
                        //   ),
                        // )
                        // :Container(),
                        // 포스트 공유 버튼 들어가는 자리
                        // Container(
                        //   child: Row(
                        //     children: [
                        //       Padding(
                        //         padding: const EdgeInsets.only(right: 2),
                        //         // 모여 컬럼
                        //         child: Text(isSwitched? '공개' : ' 비공개'),
                        //       ),
                        //       FlutterSwitch(
                        //           width: 50,
                        //           height: 30,
                        //           toggleSize: 30,
                        //           activeColor: Colors.orangeAccent.withOpacity(0.7),
                        //           activeIcon: Icon(Icons.share),
                        //           inactiveColor: Colors.grey.withOpacity(0.7),
                        //           inactiveIcon: Icon(Icons.cancel_sharp),
                        //           value: isSwitched,
                        //           onToggle: (_){
                        //             viewModel.changePostPublic(
                        //                 context,
                        //                 viewModel.post.postId
                        //             );
                        //             isSwitched
                        //             ? isSwitched = false
                        //             : isSwitched = true;
                        //           }),
                        //     ],
                        //   ),
                        // ),
                      ],
                    ),
                  );
                }),
                ChangeNotifierProvider(
                  create: (_) => AudioPlayerViewModel(viewModel.post.voiceUrl),
                  child: AudioPlayerView(),
                ),
                viewModel.post.isMoyeo == false
                    ? Container(
                  padding: const EdgeInsets.all(8),
                )
                // 동행자 이미지
                    : Container(
                    child: AvatarPile(
                      avatars: List.generate(
                        viewModel.post.members.length,
                            (index) {
                          var people = viewModel.post.members[index];
                          return CircleAvatar(
                            radius: 15,
                            backgroundImage: NetworkImage(people['profileImageUrl'].toString())
                          );
                        },
                      ),
                      title:
                      viewModel.post.members.length < 3
                      ? '${
                          viewModel.
                          post.
                          members[1]['nickname']}님 과 여행 중'
                      : '${
                          viewModel.
                          post.
                          members[1]['nickname']}님 외 '
                          '${viewModel.
                              post.
                              members.
                              length-2}명 과 동행중',
                      pileSize: 30,
                      avatarSize: 30,
                      avatarOverlap: 0.5,
                    )
                ),
                ///////////////////////////////////////////////////////////////////////////////////////
                // viewModel.post.isMoyeo == false
                //     ? Container(
                //       padding: const EdgeInsets.all(8),
                //     )
                // :InkWell(
                //   onTap: (){
                //     // 모여 나가기 기능 구현 할 곳
                //   //   {
                //   //     "userId": 0,
                //   //   "moyeoTimelineId": 0  Post에서 모여 타임라인 id 어떻게 가져오는지 물어보기
                //   // }
                //   },
                //   child: Container(
                //       width: 115,
                //       margin: const EdgeInsets.only(top:5, bottom:5, left: 20, right: 20) ,
                //       padding: const EdgeInsets.only(left: 10, right: 10, top:5, bottom: 5),
                //       decoration: BoxDecoration(
                //           boxShadow: [
                //             BoxShadow(
                //               color: Colors.grey.withOpacity(0.5),
                //               blurRadius: 1.0,
                //               spreadRadius: 1.0,
                //               offset: const Offset(2,2),
                //             )
                //           ],
                //           borderRadius: BorderRadius.circular(10),
                //           gradient: LinearGradient(
                //               colors: <Color>[
                //                 Colors.redAccent,
                //                 Colors.orangeAccent,
                //               ]
                //           )
                //       ),
                //       child: Row(
                //           children:[
                //             Icon(
                //               Icons.group_remove,
                //               color: Colors.white,
                //             ),
                //             Text(
                //               "  모여 나가기",
                //               style: TextStyle(color: Colors.white),
                //             ),
                //           ]
                //       )
                //   ),
                // ),
                //////////////////////////////////////////////////////////////////////////////////
                Text(
                    viewModel.post.text,
                  textAlign: TextAlign.start,
                )
              ],
            ),
          ),
        ],
      );
    });
  }
}
