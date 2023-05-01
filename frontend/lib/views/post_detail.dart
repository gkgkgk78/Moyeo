import 'package:animated_icon_button/animated_icon_button.dart';

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:timeline_tile/timeline_tile.dart';

import '../module/audio_player_view.dart';
import '../module/audio_player_view_model.dart';
import '../module/images_page_view.dart';
import '../view_models/app_view_model.dart';
import '../view_models/post_view_model.dart';

class PostDetail extends StatelessWidget {
  const PostDetail({super.key});

  @override
  Widget build(BuildContext context) {
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
              alignment: TimelineAlign.center,
            ),
          ),
          SizedBox(
            width: MediaQuery.of(context).size.width - 60,
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
                            insetPadding: const EdgeInsets.all(5),
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
                    height: 40,
                    child: Row(
                      crossAxisAlignment: CrossAxisAlignment.center,
                      children: [
                        AnimatedIconButton(
                          splashColor: Colors.transparent,
                          highlightColor: Colors.transparent,
                          initialIcon: viewModel.post.isFavorite ? 1 : 0,
                          onPressed: () {
                            viewModel.changeIsFavorite(
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
                        Text(viewModel.post.favoriteCount.toString()),
                      ],
                    ),
                  );
                }),
                ChangeNotifierProvider(
                  create: (_) => AudioPlayerViewModel(viewModel.post.voiceUrl),
                  child: AudioPlayerView(),
                ),
                Text(viewModel.post.text)
              ],
            ),
          ),
        ],
      );
    });
  }
}
