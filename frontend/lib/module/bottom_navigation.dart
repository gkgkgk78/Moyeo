import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../view_models/app_view_model.dart';

class CustomBottomNavigationBar extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Consumer<AppViewModel>(
      builder: (context, viewModel, child) {
        return SizedBox(
          height: 70,
          child: BottomAppBar(
            shape: const CircularNotchedRectangle(),
            notchMargin: 8.0,
            clipBehavior: Clip.antiAlias,
            child: Theme(
              data: ThemeData(
                  splashColor: Colors.transparent,
                  highlightColor: Colors.transparent),
              child: BottomNavigationBar(
                  iconSize: 25,
                  showSelectedLabels: false,
                  showUnselectedLabels: false,
                  currentIndex: viewModel.currentIndex,
                  onTap: (int index) {
                    viewModel.changePage(index);
                  },
                  items: [
                    BottomNavigationBarItem(
                      activeIcon: ShaderMask(
                        shaderCallback: (Rect bounds) {
                          return const RadialGradient(
                            center: Alignment.bottomLeft,
                            radius: 2.0,
                            colors: <Color>[
                              Colors.redAccent,
                              Colors.purpleAccent,
                              Colors.orangeAccent
                            ],
                            tileMode: TileMode.mirror,
                          ).createShader(bounds);
                        },
                        blendMode: BlendMode.srcIn,
                        child: Container(
                            margin: const EdgeInsets.only(right: 40.0),
                            child: const Icon(
                              Icons.account_circle,
                              size: 35,
                            )),
                      ),
                      icon: Container(
                          margin: const EdgeInsets.only(right: 40.0),
                          child: const Icon(Icons.account_circle_outlined,
                              color: Colors.grey)),
                      label: "내 모여",
                    ),
                    BottomNavigationBarItem(
                        activeIcon: ShaderMask(
                          shaderCallback: (Rect bounds) {
                            return const RadialGradient(
                              center: Alignment.topLeft,
                              radius: 1.0,
                              colors: <Color>[
                                Colors.red,
                                Colors.purpleAccent,
                                Colors.orangeAccent
                              ],
                              tileMode: TileMode.repeated,
                            ).createShader(bounds);
                          },
                          blendMode: BlendMode.srcIn,
                          child: Container(
                              margin: const EdgeInsets.only(left: 40.0),
                              child: const Icon(
                                Icons.backpack,
                                size: 36,
                              )),
                        ),
                        icon: Container(
                            margin: const EdgeInsets.only(left: 40.0),
                            child: const Icon(Icons.backpack_outlined, color: Colors.grey)),
                        label: "홈"),
                  ],
                  selectedItemColor: Colors.purpleAccent.shade100),
            ),
          ),
        );
      },
    );
  }
}
