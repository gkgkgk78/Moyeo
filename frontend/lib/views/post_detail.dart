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
