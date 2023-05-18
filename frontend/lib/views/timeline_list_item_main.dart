
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
        height: MyHeight*(0.6),
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
            contentMarginTop:MyHeight*(0.4),
            contentPadding: EdgeInsets.only(left: 30),
            width: MyWidth*(0.7),
            height: MyHeight*(0.6),
            borderRadius: 20,
            endColor: Colors.black,
            startColor: Colors.transparent,
            // 추후에 assetimage로 바꾸기
            imageProvider:ResizeImage(
              NetworkImage(
                timeline.imageUrl.isNotEmpty
                    ? timeline.imageUrl
                    :'assets/images/default_image.png',
              ),
              width: 200,
              allowUpscaling: true,
            ),
            title: Text(
                timeline.title,
                overflow: TextOverflow.ellipsis,
                style: TextStyle(
                    color: Colors.white,
                    fontSize:30,
                    fontWeight:FontWeight.bold
                )
            ),
          description: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            children: [
              Container(
                width: MyWidth*(0.5),
                margin: EdgeInsets.all(5),
                padding: EdgeInsets.only(left: 0),
                child:Text(
                timeline.nickname,
                style:TextStyle(
                    color: Colors.white,
                    fontSize:15,
                ) ,
              )
              ),
              Container(
                  width: MyWidth*(0.5),
                  margin: EdgeInsets.all(5),
                  padding: EdgeInsets.only(left: 0),
                  child:Text(
               "${timeline.createTime} ~ ${timeline.finishTime}",
                style: TextStyle(
                    color: Colors.white,
                    fontSize:15,
                ),
              )),
              Container(
                  width: MyWidth*(0.5),
                  margin: EdgeInsets.all(5),
                  padding: EdgeInsets.only(left: 0),
                  child:Text(
                  "${timeline.startPlace} ~ ${timeline.finishPlace}",
                  style:TextStyle(
                      color: Colors.white,
                      fontSize:15,
                  )
              ))// Text(data)
            ],
          ),
          ),
          ),
        );
    });
  }
}
