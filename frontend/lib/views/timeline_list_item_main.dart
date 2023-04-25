import 'package:danim/models/Timeline.dart';
import 'package:extended_image/extended_image.dart';
import 'package:flutter/material.dart';
import 'package:image_card/image_card.dart';
import 'package:provider/provider.dart';

import '../view_models/app_view_model.dart';

class TimelineListItemMain extends StatelessWidget {
  final Timeline timeline;

  const TimelineListItemMain({super.key, required this.timeline});


  @override
  Widget build(BuildContext context) {
    const cardHeight = 300.0;
    return Consumer<AppViewModel>(builder: (_, appViewModel, __) {
      return Container(
        margin: const EdgeInsets.only(bottom: 5, left: 5, right: 5),
        height: cardHeight,
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
            width: 300,
            imageProvider: AssetImage(
              timeline.imageUrl.isNotEmpty
                ? timeline.imageUrl
                :'assets/images/default_image.png'
            ),
            title: Text(timeline.title),
            ),
          ),
        );
    });
  }
}
