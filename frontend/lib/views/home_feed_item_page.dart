
import 'package:flutter/material.dart';
import 'package:infinite_scroll_pagination/infinite_scroll_pagination.dart';
import 'package:provider/provider.dart';

import '../models/Timeline.dart';
import '../view_models/app_view_model.dart';
import 'home_feed_item.dart';

class HomeFeedItemPage extends StatelessWidget{
  final PagingController<int, Timeline> pagingController;

  const HomeFeedItemPage({super.key, required this.pagingController});

  @override
  Widget build(BuildContext context) => Consumer<AppViewModel>(
      builder: (_, appViewModel, __){
        return RefreshIndicator(
            onRefresh: () async {
              pagingController.refresh();
            },
            child: PagedListView<int,Timeline>(
              shrinkWrap: true,
              physics: const AlwaysScrollableScrollPhysics() ,
              scrollDirection: Axis.vertical,
              pagingController: pagingController,
              builderDelegate: PagedChildBuilderDelegate<Timeline>(
                itemBuilder: (context, item, index) => HomeFeedItem(
                  key: Key(item.timelineId.toString()),
                  timeline:item,
                )
              ),
        ),);
      });
}