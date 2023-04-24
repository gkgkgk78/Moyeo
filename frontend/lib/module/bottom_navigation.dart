import 'package:animated_bottom_navigation_bar/animated_bottom_navigation_bar.dart';
import 'package:danim/view_models/app_view_model.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class CustomBottomNavigationBar extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Consumer<AppViewModel>(
      builder: (context, viewModel, child) {
        return AnimatedBottomNavigationBar(
          icons: const [Icons.home, Icons.account_circle],
          iconSize: 40,
          activeIndex: viewModel.currentIndex,
          gapLocation: GapLocation.center,
          notchSmoothness: NotchSmoothness.softEdge,
          onTap: (index) {
            viewModel.changePage(index);
          },
          activeColor: Colors.orangeAccent,
        );
      },
    );
  }
}
