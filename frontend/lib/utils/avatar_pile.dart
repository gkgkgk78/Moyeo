import 'package:flutter/material.dart';

class AvatarPile extends StatelessWidget {
  final List<Widget> avatars;
  final String title;
  final double pileSize;
  final double avatarSize;
  final double avatarOverlap;

  const AvatarPile({super.key,
    required this.avatars,
    required this.title,
    required this.pileSize,
    required this.avatarSize,
    this.avatarOverlap = 0.3,
  });

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: MediaQuery.of(context).size.width*(0.55),
      height: pileSize,
      child: Row(
        children: [
          Text(
            title,
            overflow: TextOverflow.ellipsis,
            style: TextStyle(
          fontSize: MediaQuery.of(context).size.height*(0.017)
            ),
          ),
          Expanded(child: Container()),
          for (int i = 0; i < avatars.length; i++)
            Container(
              margin: const EdgeInsets.only(left: 1),
              child: avatars[i],
            ),
        ],
      ),
    );
  }
}
