
import 'package:extended_image/extended_image.dart';

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../models/Timeline.dart';
import '../view_models/app_view_model.dart';



class HomeFeedItem extends StatelessWidget{
  final Timeline timeline;

  const HomeFeedItem({Key? key, required this.timeline}) : super(key:key);

  @override
  Widget build(BuildContext context){
    final MyHeight = MediaQuery.of(context).size.height;
    final MyWidth = MediaQuery.of(context).size.width;

    return Consumer<AppViewModel>(builder: (_, appViewModel, __){
      return GridView.builder(
        gridDelegate:
        const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 3,
        ),
        scrollDirection: Axis.vertical,
        itemBuilder: (BuildContext context, int index){
          return GridTile(
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  Stack(
                    children: [
                      SizedBox(
                        width: 100,
                        height: 100,
                        child: GestureDetector(
                          child: ExtendedImage.network(
                            borderRadius: BorderRadius.circular(15),
                            fit: BoxFit.cover,
                            timeline.imageUrl.toString()
                          ),
                          onTap: (){
                            appViewModel.changeTitle(
                              timeline.title.toString()
                            );
                            Navigator.pushNamed(context,
                              '/timeline/detail/${timeline.timelineId}');
                          },
                        ),
                      ),
                      Positioned(
                        top: MyHeight*(0.8),
                        left: MyWidth*(0.1),
                        child: Container(
                          decoration: BoxDecoration(
                            color: Colors.black54,
                            borderRadius:
                              BorderRadius.circular(10)
                            ),
                          width: 50,
                          height: 25,
                          child: Row(
                            mainAxisAlignment:
                            MainAxisAlignment.spaceEvenly,
                            children: const [
                              const Icon(
                                Icons.favorite,
                                color: Colors.redAccent,
                                size: 12,
                              ),
                              Text("테스트")
                            ],
                          ),
                        ),
                      ),
                    ],
                  ),
                ],
              ),
          );
        },
      );
    });
  }
}