
import 'package:extended_image/extended_image.dart';
import 'package:flutter/material.dart';
import 'package:moyeo/views/search_bar_view.dart';
import 'package:provider/provider.dart';

import '../view_models/app_view_model.dart';
import '../view_models/search_bar_view_model.dart';
import '../view_models/search_result_view_model.dart';

class SearchResultView extends StatelessWidget {
  const SearchResultView({super.key});

  @override
  Widget build(BuildContext context) {
    return Consumer<AppViewModel>(builder: (_, appViewModel, __) {
      return Consumer<SearchResultViewModel>(
        builder: (context, viewModel, _) {
          return Scaffold(
            body: LayoutBuilder(
              builder: (BuildContext context, BoxConstraints constraints) {
                return GestureDetector(
                  behavior: HitTestBehavior.translucent,
                  onTap: () {
                    FocusScope.of(context).unfocus();
                  },
                  child: Stack(
                    children: [
                      Padding(
                        padding: const EdgeInsets.only(top: 70, right:5, left:5),
                        child: viewModel.searchedPosts.isEmpty
                            ? Center(
                                child: Text(
                                    "${viewModel.keyword} 지역에 대한 검색 결과가 없습니다."))
                            : GridView.builder(
                                itemCount: viewModel.searchedPosts.length,
                                gridDelegate:
                                    const SliverGridDelegateWithFixedCrossAxisCount(
                                  crossAxisCount: 3,
                                ),
                                scrollDirection: Axis.vertical,
                                itemBuilder: (BuildContext context, int index) {
                                  return GridTile(
                                    child: Row(
                                      mainAxisAlignment:
                                          MainAxisAlignment.spaceEvenly,
                                      children: [
                                        Stack(
                                          children: [
                                            SizedBox(
                                              width:
                                                  constraints.maxWidth * 0.32,
                                              height:
                                                  constraints.maxWidth * 0.32,
                                              child: GestureDetector(
                                                child: ExtendedImage.network(
                                                  borderRadius: BorderRadius.circular(15),
                                                  fit: BoxFit.cover,
                                                  viewModel.searchedPosts[index]
                                                      .thumbnailUrl,
                                                ),
                                                onTap: () {
                                                  appViewModel.changeTitle(
                                                      viewModel
                                                          .searchedPosts[index]
                                                          .timelineTitle!);
                                                  Navigator.pushNamed(context,
                                                      '/timeline/detail/${viewModel.searchedPosts[index].timelineId}');
                                                },
                                              ),
                                            ),
                                            Positioned(
                                              top: constraints.maxWidth * 0.25,
                                              left: constraints.maxWidth * 0.19,
                                              child: Container(
                                                decoration: BoxDecoration(
                                                  color: Colors.black54,
                                                  borderRadius:
                                                      BorderRadius.circular(10),
                                                ),
                                                width: 50,
                                                height: 25,
                                                child: Row(
                                                  mainAxisAlignment:
                                                      MainAxisAlignment
                                                          .spaceEvenly,
                                                  children: [
                                                    const Icon(
                                                      Icons.favorite,
                                                      color: Colors.redAccent,
                                                      size: 12,
                                                    ),
                                                    Text(
                                                      '${viewModel.searchedPosts[index].favorite}',
                                                      style: const TextStyle(
                                                        color: Colors.white,
                                                        fontSize: 12,
                                                      ),
                                                    )
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
                              ),
                      ),
                      Positioned(
                        top: 0,
                        left: 0,
                        right: 0,
                        bottom: 0,
                        child: Container(
                          margin: const EdgeInsets.only(
                              left: 10, right: 10, top: 15, bottom: 15),
                          child: ChangeNotifierProvider<SearchBarViewModel>(
                            create: (_) => SearchBarViewModel(
                                isMyFeed: viewModel.isMyFeed),
                            child: const SearchBar(),
                          ),
                        ),
                      ),
                    ],
                  ),
                );
              },
            ),
          );
        },
      );
    });
  }
}
