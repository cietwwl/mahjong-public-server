package com.randioo.mahjong_public_server.module.gm.service;

import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.randioo.mahjong_public_server.GlobleConstant;
import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.module.close.service.CloseService;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.config.GlobleConfig;
import com.randioo.randioo_server_base.config.GlobleConfig.GlobleEnum;
import com.randioo.randioo_server_base.config.GlobleMap;
import com.randioo.randioo_server_base.db.GameDB;
import com.randioo.randioo_server_base.entity.RoleInterface;
import com.randioo.randioo_server_base.platform.Platform;
import com.randioo.randioo_server_base.platform.Platform.OS;
import com.randioo.randioo_server_base.platform.SignalTrigger;
import com.randioo.randioo_server_base.scheduler.SchedulerManager;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.template.EntityRunnable;
import com.randioo.randioo_server_base.template.Function;
import com.randioo.randioo_server_base.utils.StringUtils;

@Service("gmService")
public class GmServiceImpl extends ObserveBaseService implements GmService {

	@Autowired
	private SchedulerManager schedulerManager;

	@Autowired
	private GameDB gameDB;

	@Autowired
	private CloseService closeService;

	@Override
	public void init() {

		Function function = new Function() {

			@Override
			public Object apply(Object... params) {
				GlobleMap.putParam(GlobleConstant.ARGS_LOGIN, false);

				loggerinfo("port close");

				everybodyOffline();

				// 定时器全部停止
				try {
					schedulerManager.shutdown(1, TimeUnit.SECONDS);
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				try {
					gameDB.shutdown(1, TimeUnit.SECONDS, new Function() {

						@Override
						public Object apply(Object... params) {
							// 数据保存
							loggerinfo("start game all data save");
							// 循环性数据存储
							ExecutorService service = loopSaveData(true, true, 2);

							service.shutdown();
							try {
								while (!service.awaitTermination(1, TimeUnit.SECONDS)) {
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							// 一次性数据存储
							onceSaveData();

							loggerinfo("game all data save SUCCESS");
							System.exit(0);
							return null;
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}

				return null;
			}

		};

		// 命令关闭信号
		try {
			loggerinfo(Platform.getOS().toString());
			if (Platform.getOS() == OS.WIN)
				SignalTrigger.setSignCallback("INT", function);
			else if (Platform.getOS() == OS.LINUX)
				SignalTrigger.setSignCallback("ABRT", function);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (Platform.getOS() == OS.WIN) {
			Thread t = new Thread(new EntityRunnable<Function>(function) {

				private Scanner in = new Scanner(System.in);

				@Override
				public void run(Function function) {
					while (true) {
						try {
							String command = in.nextLine();
							if (!StringUtils.isNullOrEmpty(command)) {
								if (command.equals("exit")) {
									function.apply();
								} else if (command.equals("save")) {
									for (RoleInterface roleInterface : RoleCache.getRoleMap().values()) {
										closeService.roleDataCache2DB((Role) roleInterface, true);
										loggerinfo(roleInterface.getAccount() + " force Save");
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

			});

			// t.start();

		}
	}

	/**
	 * 所有人下线
	 * 
	 * @author wcy 2016年12月9日
	 */
	private void everybodyOffline() {
		// 所有人下线
		Collection<IoSession> allSession = SessionCache.getAllSession();
		Iterator<IoSession> it = allSession.iterator();
		while (it.hasNext()) {
			it.next().close(true);
		}

	}

	@Override
	public void loopSaveData(boolean mustSave) {
		loopSaveData(mustSave, false, 0);
	}

	private ExecutorService loopSaveData(final boolean mustSave, boolean thread, int threadSize) {
		ExecutorService executorService = null;
		if (thread) {
			executorService = Executors.newScheduledThreadPool(threadSize);
		}
		for (RoleInterface roleInterface : RoleCache.getRoleMap().values()) {
			if (executorService != null) {
				executorService.submit(new EntityRunnable<RoleInterface>(roleInterface) {

					@Override
					public void run(RoleInterface roleInterface) {
						saveRoleData(roleInterface, mustSave);
					}
				});
			} else {
				saveRoleData(roleInterface, mustSave);
			}
		}

		return executorService;

	}

	private void saveRoleData(RoleInterface roleInterface, boolean mustSave) {
		try {
			Role role = (Role) roleInterface;
			if (mustSave) {
				loggerinfo("id:" + role.getRoleId() + ",account:" + role.getAccount() + ",name:" + role.getName()
						+ "] save success");
			}
			closeService.roleDataCache2DB(role, mustSave);
		} catch (Exception e) {
			loggererror("Role: " + roleInterface.getRoleId() + " saveError!", e);
			e.printStackTrace();
		}
	}

	private void onceSaveData() {

	}

}
