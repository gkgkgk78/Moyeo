
import 'package:extended_image/extended_image.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'package:image_card/image_card.dart';
import 'package:card_swiper/card_swiper.dart';

import '../models/Timeline.dart';
import '../view_models/app_view_model.dart';

class TimelineListItemMain extends StatelessWidget {
  final Timeline timeline;

  const TimelineListItemMain({super.key, required this.timeline});


  @override
  Widget build(BuildContext context) {
    final MyHeight = MediaQuery.of(context).size.height;
    final MyWidth = MediaQuery.of(context).size.width;
    // final cardHeight = 300;
    return Consumer<AppViewModel>(builder: (_, appViewModel, __) {
      return Container(
        width: MyWidth*(0.7),
        height: MyHeight*(0.65),
        margin: EdgeInsets.only(top:MyHeight*(0.01), left: MyWidth*(0.1), right: MyWidth*(0.1)),
        child: GestureDetector(
          onTap: () {
            FocusScope.of(context).unfocus();
            appViewModel.changeTitle(timeline.title);
            Navigator.pushNamed(
              context,
              '/timeline/detail/${timeline.timelineId}',
            );
          },
          child: TransparentImageCard(
            width: MyWidth*(0.7),
            height: MyHeight*(0.65),
            borderRadius: 20,
            // 추후에 assetimage로 바꾸기
            imageProvider: NetworkImage(
              timeline.imageUrl.isNotEmpty
                ? timeline.imageUrl
                :'assets/images/default_image.png'
            ),
            title: Text(
                timeline.title,
                style: TextStyle(
                    color: Colors.white,
                    fontSize:20,
                    fontWeight:FontWeight.bold
                )),
            ),
          ),
        );
    });
  }
}
