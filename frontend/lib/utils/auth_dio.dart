
import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

import '../views/login_page.dart';

Future<Dio> authDio(BuildContext context) async {
  final baseUrl = dotenv.env['baseUrl'];
  final dio = Dio(BaseOptions(baseUrl: baseUrl!));

  const storage = FlutterSecureStorage();

  dio.interceptors.clear();

  dio.interceptors.add(
    LogInterceptor(
        responseBody: true,
        error: true,
        requestHeader: false,
        responseHeader: false,
        request: false,
        requestBody: false),
  );

  dio.interceptors.add(
    InterceptorsWrapper(
      onRequest: (options, handler) async {
        final accessToken = await storage.read(key: 'accessToken');
        options.headers['Authorization'] = 'Bearer $accessToken';
        return handler.next(options);
      },
      onError: (error, handler) async {
        // accessToken 만료 됐을 때
        // 401이 SocketException 으로 와서 어쩔 수 없이 이렇게 처리
        if (error.response == null || error.response?.statusCode == 401) {
          final String? refreshToken = await storage.read(key: 'refreshToken');
          final String? userUid = await storage.read(key: 'userUid');

          final refreshDio = Dio(BaseOptions(baseUrl: baseUrl));
          Response response = await refreshDio.post(
            'api/login/reissuance',
            options: Options(headers: {'refreshToken': 'Bearer $refreshToken'}),
            data: {'userUid': int.parse(userUid ?? '')},
          );
          if (response.statusCode! < 400) {
            final String? newAccessToken =
                response.headers['Authorization']?[0].split(' ')[1];
            storage.write(key: 'accessToken', value: newAccessToken);
            final clonedRequest = await dio.request(error.requestOptions.path,
                options: Options(
                    method: error.requestOptions.method,
                    headers: {'Authorization': 'Bearer $newAccessToken'}),
                data: error.requestOptions.data,
                queryParameters: error.requestOptions.queryParameters);
            return handler.resolve(clonedRequest);
          } else {
            if (context.mounted) {
              Navigator.push(
                context,
                PageRouteBuilder(
                  pageBuilder: (_, __, ___) => LoginPage(),
                ),
              );
            }
          }
          return handler.next(error);
        }
      },
    ),
  );
  return dio;
}
