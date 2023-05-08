import 'package:circular_menu/circular_menu.dart';
import 'package:flutter/material.dart';
import 'package:logger/logger.dart';

var logger = Logger();

class PushAlarmPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Stack(
          children: [
            Center(
              child: Container(
                alignment: Alignment.center,
                width: 70,
                height: 70,
                decoration: BoxDecoration(
                    shape: BoxShape.circle,
                  gradient: LinearGradient(
                      colors: [
                        Colors.redAccent,
                        Colors.orangeAccent,
                      ]
                  )
                ),
              ),
            ),
            CircularMenu(
              alignment: Alignment.center,
              toggleButtonSize: 50,
                toggleButtonColor: Colors.transparent,
                toggleButtonAnimatedIconData: AnimatedIcons.ellipsis_search,
                items: [
                  CircularMenuItem(
                      color: Colors.orangeAccent,
                      icon: Icons.smart_toy_outlined,
                      onTap: () {
                        logger.d("테스트");
                      }),
                  CircularMenuItem(color: Colors.purpleAccent,icon: Icons.camera, onTap: () {logger.d("테스트");}),
                ]),
          ],
        ),
      ),
    );
  }
}