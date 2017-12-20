package s2wmp.services;

import java.sql.Date;
import java.util.Map;

import jdk.nashorn.internal.ir.RuntimeNode.Request;
import s2wmp.UserID;

/**
 * Class: RequestCleanupService
 * date: 06/02/2016
 * @author Sergey Matskevich, Scott McHenry, Zainul Din, Marcos Zegarra
 *	Deletes old requests
 */
public class RequestCleanupService implements Runnable
{
	Map<UserID, Long> requests;
	public RequestCleanupService(Map<UserID, Long> r)
	{
		requests = r;
	}
	@Override
	public void run()
	{
		try
		{
			Thread.sleep(10000);
		} catch (InterruptedException e)
		{}
		long cur = System.currentTimeMillis();
		for(UserID id : requests.keySet())
		{
			if((requests.get(id) - cur) > 10000)
			{
				requests.remove(id);
			}
		}

	}

}
