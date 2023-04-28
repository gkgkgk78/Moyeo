import 'package:flutter/material.dart';

class GradientCircularProgressIndicator extends StatelessWidget {
  const GradientCircularProgressIndicator({super.key});

  @override
  Widget build(BuildContext context) {
    return ShaderMask(
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
      child: SizedBox(
        height: 50,
          width: 50,
          child: Transform.scale(
            scale: 0.9,
            child: const CircularProgressIndicator(
              strokeWidth: 20.0,
              backgroundColor: Colors.transparent,
            ),
          )
      ),
    );
  }


}