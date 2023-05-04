import 'dart:math' as math;

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../view_models/app_view_model.dart';
import '../views/camera_screen.dart';

@immutable
class ExpandableFab extends StatefulWidget{
  const ExpandableFab({
    super.key,
    this.initialOpen,
    required this.distance,
    required this.children,
  });

  final bool? initialOpen;
  final double distance;
  final List<Widget> children;

  @override
  State<ExpandableFab> createState() => _ExpandableFabState();
}

class _ExpandableFabState extends State<ExpandableFab>
    with SingleTickerProviderStateMixin{
  late final AnimationController _controller;
  late final Animation<double> _expandAnimation;
  bool _open = false;

  @override
  void initState() {
    super.initState();
    _open = widget.initialOpen ?? false;
    _controller = AnimationController(
      value: _open ? 1.0: 0.0,
        duration: const Duration(milliseconds: 250),
        vsync: this,
    );
    _expandAnimation = CurvedAnimation(
        parent: _controller,
        curve: Curves.fastOutSlowIn,
        reverseCurve:Curves.easeOutQuad,
    );
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  void _toggle() {
    setState(() {
      _open = !_open;
      if (_open) {
        _controller.forward();
      } else {
        _controller.reverse();
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return SizedBox.expand(
      child: Stack(
        alignment: Alignment.bottomRight,
        clipBehavior: Clip.none,
        children: [
          _buildTapToCloseFab(),
          ..._buildExpandingActionButtons(),
          _buildTapToOpenFab(),
        ],
      ),
    );
  }

  Widget _buildTapToCloseFab() {
    return SizedBox(
      width: 70.0,
      height: 70.0,
      child: Center(
        child: Material(
          shape: const CircleBorder(),
          clipBehavior: Clip.antiAlias,
          elevation: 4.0,
          child: Container(
            width: 50,
            height: 50,
            child:FittedBox(
              child:InkWell(
                onTap: _toggle,
                child: Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: Icon(
                    Icons.close,
                    color: Colors.redAccent,
                  ),
                ),
              ),
            )
          )
        ),
      ),
    );
  }

  List<Widget> _buildExpandingActionButtons() {
    final children = <Widget>[];
    final count = widget.children.length;
    final step = 180.0 / (count - 1);
    for (var i = 0, angleInDegrees = 0.0;
    i < count;
    i++, angleInDegrees += step) {
      children.add(
        _ExpandingActionButton(
          directionInDegrees: angleInDegrees,
          maxDistance: widget.distance,
          progress: _expandAnimation,
          child: widget.children[i],
        ),
      );
    }
    return children;
  }

  Widget _buildTapToOpenFab() {
    return IgnorePointer(
      ignoring: _open,
      child: AnimatedContainer(
        transformAlignment: Alignment.center,
        transform: Matrix4.diagonal3Values(
          _open ? 0.9 : 1.0,
          _open ? 0.9 : 1.0,
          1.0,
        ),
        duration: const Duration(milliseconds: 250),
        curve: const Interval(0.0, 0.5, curve: Curves.easeOut),
        child: AnimatedOpacity(
          opacity: _open ? 0.0 : 1.0,
          curve: const Interval(0.25, 1.0, curve: Curves.easeInOut),
          duration: const Duration(milliseconds: 250),
          child: Container(
            height: 100,
            width: 100,
            decoration: BoxDecoration(
            shape: BoxShape.circle,
            gradient: LinearGradient(
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
                colors: [
                  Colors.redAccent,
                  Colors.orangeAccent,
                ]
            )
        ),
            child:FittedBox(
              child:FloatingActionButton(
                elevation: 0,
                backgroundColor: Colors.transparent,
                onPressed: _toggle,
                child: const Icon(Icons.home_filled, color: Colors.white),
              )
            ),
          )
        ),
      ),
    );
  }
}

@immutable
class _ExpandingActionButton extends StatelessWidget {
  const _ExpandingActionButton({
    required this.directionInDegrees,
    required this.maxDistance,
    required this.progress,
    required this.child,
  });

  final double directionInDegrees;
  final double maxDistance;
  final Animation<double> progress;
  final Widget child;

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: progress,
      builder: (context, child) {
        final offset = Offset.fromDirection(
          directionInDegrees * (math.pi / 180.0),
          progress.value * maxDistance,
        );
        return Positioned(
          right: 10.0 + offset.dx,
          bottom: 10.0 + offset.dy,
          child: Transform.rotate(
            angle: (1.0 - progress.value) * math.pi,
            child: child!,
          ),
        );
      },
      child: FadeTransition(
        opacity: progress,
        child: child,
      ),
    );
  }
}

@immutable
class ActionButton extends StatelessWidget {
  const ActionButton({
    super.key,
    this.onPressed,
    required this.icon,
  });

  final VoidCallback? onPressed;
  final Widget icon;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Material(
      shape: const CircleBorder(),
      clipBehavior: Clip.antiAlias,
      color: theme.colorScheme.secondary,
      elevation: 4.0,
      child: IconButton(
        onPressed: onPressed,
        icon: icon,
        color: Colors.black,
      ),
    );
  }
}

@immutable
class FakeItem extends StatelessWidget {
  const FakeItem({
    super.key,
    required this.isBig,
  });

  final bool isBig;

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.symmetric(vertical: 8.0, horizontal: 24.0),
      height: isBig ? 128.0 : 36.0,
      decoration: BoxDecoration(
        borderRadius: const BorderRadius.all(Radius.circular(8.0)),
        color: Colors.grey.shade300,
      ),
    );
  }
}

class CameraFloatingActionButton extends StatelessWidget {
  const CameraFloatingActionButton({super.key});

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 70.0,
      width: 70.0,
      child:Center(
      // 이전 FAB일 때
      // FittedBox(
      //   child: Consumer<AppViewModel>(
      //     builder: (_, appViewModel, __) {
      //       return
        child:Scaffold(
          floatingActionButtonLocation: FloatingActionButtonLocation.centerTop,
              floatingActionButton:ExpandableFab(
                distance: 70,
                children: [
                  ActionButton(
                      icon:const Icon(Icons.smart_toy)
                  ),
                  ActionButton(
                      icon:const Icon(Icons.photo_camera)
                  ),
                  ActionButton(
                      icon: const Icon(Icons.leave_bags_at_home_outlined)
                  ),
                  ActionButton(
                      icon: const Icon(Icons.logout, color: Colors.redAccent)
                  ),
                ]
            )
            )

              // FAB 일때
              // onPressed: () {
              //   Navigator.push(
              //     context,
              //     MaterialPageRoute(
              //       builder: (context) => CameraView(),
              //     ),
              //   );
              // },
            //   child: Container(
            //     height: 70,
            //     width: 70,
            //     decoration: const BoxDecoration(
            //         shape: BoxShape.circle,
            //         gradient: LinearGradient(
            //             begin: Alignment.topLeft,
            //             end: Alignment.bottomRight,
            //             colors: [
            //               Colors.redAccent,
            //               Colors.orangeAccent,
            //             ]
            //         )
            //     ),
            //     child: const Icon(
            //       Icons.camera,
            //       color: Colors.white,
            //       size: 40,
            //     ),
            //   ),
            // );

            //   FloatingActionButton(
            //   child: appViewModel.userInfo.timeLineId == -1
            //       ? Image.asset(
            //           'assets/images/transparent_logo.png',
            //           width: 50,
            //           height: 50,
            //         )
            //       : const Icon(
            //           Icons.camera,
            //           color: Colors.white,
            //         ),
            //   onPressed: () {
            //     if (appViewModel.userInfo.timeLineId == -1) {
            //       // 여행 중이 아닐 때 여행 시작
            //       appViewModel.startTravel(context);
            //     } else {
            //       // 여행 중일 때 사진 촬영 화면으로 이동
            //       Navigator.push(
            //         context,
            //         MaterialPageRoute(
            //           builder: (context) => ChangeNotifierProvider(
            //             create: (_) => CameraViewModel(),
            //             child: const CameraView(),
            //           ),
            //         ),
            //       );
            //     }
            //   },
            // );

          // },
        // ),
      // ),
    ));
  }
}
