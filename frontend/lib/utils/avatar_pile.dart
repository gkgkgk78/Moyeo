import 'package:flutter/material.dart';

class AvatarPile extends StatelessWidget {
  final List<Widget> avatars;
  final String title;
  final double pileSize;
  final double avatarSize;
  final double avatarOverlap;

  AvatarPile({
    required this.avatars,
    required this.title,
    required this.pileSize,
    required this.avatarSize,
    this.avatarOverlap = 0.3,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 200,
      height: pileSize,
      child: Stack(
        children: [
          Container(
            padding: EdgeInsets.only(top:5),
              child:Text(
            title,
            style: TextStyle(
              fontWeight: FontWeight.bold
            ),
          )
          ),
          Expanded(child: Container()),
          for (int i = 0; i < avatars.length; i++)
            Positioned(
              right: i * avatarOverlap * avatarSize,
              child: avatars[i],
            ),
        ],
      ),
    );
  }
}
