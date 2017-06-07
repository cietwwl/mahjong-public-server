package com.randioo.mahjong_public_server.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Tool {
	
	public static boolean regExpression(String str, String rule){
        Pattern pattern = Pattern.compile(rule);
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
	}
	public static Method getFunction(@SuppressWarnings("rawtypes") Class $class , String funcname)
	{
	    Method method = null;
	    Method[] methodList =   $class.getDeclaredMethods();
		for ( int i = 0 ; i < methodList.length ; i ++)
		{
			Method m = methodList[i];
			if (m.getName().equals(funcname))
			{
				method = m;
			}
		}
	    return method;
	}
	
	/**打乱数组顺序*/
	public static int[] randomList(int[] $list)
	{
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		List<Integer> list=new ArrayList();  
		for(int t=0; t  < $list.length; t++){    
			list.add($list[t]);    
		}  
		
		int [] copytmp = new int[list.size()];
		Random random = new Random();
		int len = list.size();
		
		for (int i = 0 ; i < len; i ++)
		{
			int _index = random.nextInt(list.size());
			copytmp[i] = (int) list.get(_index);
			list.remove(_index);
		}
		return copytmp;
	}
	public static int [] arrayCopy(int [] src , int[] dest)
	{
		if (dest == null)return src;
		int [] list = new int[src.length + dest.length];
		System.arraycopy( src, 0, list, 0, src.length);
		System.arraycopy( dest, 0, list, src.length, dest.length);
		return list;
	}
	
	public static int [] arrayCopy(int [] src , int[] dest, int loc)
	{
		if (dest == null)return src;
		int [] list = new int[src.length + dest.length];
		
		System.arraycopy( dest, 0, list, 0, 13*loc);
		System.arraycopy( src, 0, list, 13*loc, src.length);
		System.arraycopy( dest, 13*loc, list, 13*loc+src.length, dest.length-13*loc);
		return list;
	}
	
	/**打乱数组顺序*/
	public static String[] randomList(String[] $list)
	{
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<String> list=new ArrayList();  
		for(String t : $list){    
			list.add(t);    
		}  
		String [] copytmp = new String[list.size()];
		Random random = new Random();
		int len = list.size();
		
		for (int i = 0 ; i < len; i ++)
		{
			int _index = random.nextInt(list.size());
			copytmp[_index] = (String) list.get(_index);
			list.remove(_index);
		}
		return copytmp;
	}
	
	public static int indexOf(int [] $list , int $target)
	{
		int len = $list.length;
		
		for (int i = 0 ; i < len; i ++)
		{
			if ($list[i] == $target)
			{
				return i;
			}
		}
		return -1;
	}
	/**依据某个索引删除数组一个元素*/
	public static int[] removeItemForList(int[] $list, int $index)
	{
		
		int[] tmp = new int[$list.length - 1];
		int len = $list.length;
		int index = 0;
		for (int i = 0 ; i < len; i++)
		{
			if (i!=$index)
			{
				tmp[index] = $list[i];
				index++;
			}
		}
		return tmp;
		
	}
	public static int[] removeItemForList2(int[] $list, int $index)
	{
		if($index == -1)return $list;
		return removeItemForList($list,$index);
	}
	/**依据某个索引删除数组一个元素*/
	public static int[] removeItem(int[] $list, int $deleteTarget)
	{
		int[] tmp = new int[$list.length - 1];
		int len = $list.length;
		int index = 0;
		boolean isDelete = false;
		for (int i = 0 ; i < len; i++)
		{
			if (isDelete== false && $list[i] == $deleteTarget)
			{
				isDelete = true;
				continue;
			}
			tmp[index] = $list[i];
			index++;
		}
		return tmp;
	}
	/**取含四个相同的数字*/
	public static int getSame4(int [] $list , int $target)
	{
		int [] tList = $list.clone();
		tList = addItemToList(tList, $target);
		Arrays.sort(tList);
		int len = tList.length -3;
		for(int i = 0; i < len ;i++ )
		{
			if (tList[i] == tList[i+1] && tList[i+2] == tList[i+1]&& tList[i+2] == tList[i+3])
			{
				return tList[i];
			}
		}
		
		return -1;
	}
	/**依据某个索引删除数组一个元素*/
	public static String[] removeItemForList(String[] $list, String comp)
	{
		String[] tmp = new String[$list.length - 1];
		int len = $list.length;
		int index = 0;
		for (int i = 0 ; i < len; i++)
		{
			if ($list[i].indexOf(comp) > -1)
			{
				continue;
			}
			tmp[index] = $list[i];
			index++;
		}
		return tmp;
	}
	
	public static boolean theItemIsInList(int [] $list, int checkTarget)
	{
		int len = $list.length;
		for (int i = 0 ; i < len; i++)
		{
			if ($list[i] == checkTarget)
			{
				return true;
			}
		}
		return false;
	}
	
	public static int [] addItemToList(int [] $list,int addTarget)
	{
		int[] tmp = new int[$list.length + 1];
		int len = $list.length;
		for (int i = 0 ; i < len; i++)
		{
			tmp[i] = $list[i];
		}
		tmp[$list.length] = addTarget;
		return tmp;
	}
	public static ArrayList<Integer> chang(int[] $list)
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		int len = $list.length;
		for (int i = 0 ; i < len; i ++)
		{
			list.add($list[i]);
		}
		
		list.remove(0);
		return list;
	}
	/***/
	public static int [] remove123(int [] _list)
	{
		Arrays.sort(_list);
		if (_list.length< 3)
		{
			return _list;
		}
		for (int i = 0; i< _list.length ; )
		{
			int num0 = _list[i];
			if (num0 > 400)
			{
				i++;
				continue;
			}
			int num1 = Tool.indexOf(_list, num0+1);
			int num2 = Tool.indexOf(_list, num0+2);
			if (num1 != -1 && num2 != -1)
			{
				_list = Tool.removeItem(_list, num0);
				_list = Tool.removeItem(_list, num0+1);
				_list = Tool.removeItem(_list, num0+2);
				i = 0;
				continue;
			}
			i++;
		}
		return _list;
	}

	/**移除所以三个相同的。 */
	public static int [] removeBase3(int[] _list)
	{
		Arrays.sort(_list);
		if (_list.length< 3)
		{
			return _list;
		}
		int maxIndex = _list.length -2;
		for (int i = 0; i< maxIndex ;)
		{
			int num0 = _list[i];
			if ( _list[i+1] == _list[i+2] && _list[i+2] == num0)
			{
				_list = Tool.removeItem(_list, num0);
				_list = Tool.removeItem(_list, num0);
				_list = Tool.removeItem(_list, num0);
				maxIndex = _list.length -2;
				i = 0;
				continue;
			}
			i++;
		}
		return _list;
	}
	
	/**移除所以二个相同的。 */
	public static int [] removeBase2(int[] _list)
	{
		Arrays.sort(_list);
		if (_list.length< 3)
		{
			return _list;
		}
		int maxIndex = _list.length -1;
		for (int i = 0; i< maxIndex ;)
		{
			int num0 = _list[i];
			if ( _list[i+1] == num0)
			{
				_list = Tool.removeItem(_list, num0);
				_list = Tool.removeItem(_list, num0);
				maxIndex = _list.length -1;
				i = 0;
				continue;
			}
			i++;
		}
		return _list;
	}
	
	
	


	
	
	
	
	public static int [] removeLlist(int [] list ,int[] delete)
	{
		int [] cList = list.clone();
		for (int i = 0 ; i < delete.length; i ++)
		{
			if(indexOf(cList, delete[i])<-1)
			{	
				return list;
			}
			cList = removeItem(cList, delete[i]);
		}
		return cList;
	}
	


	public static byte[] intToByteArray1(int i) {   
		byte[] result = new byte[4];   
		result[0] = (byte)((i >> 24) & 0xFF);
		result[1] = (byte)((i >> 16) & 0xFF);
		result[2] = (byte)((i >> 8) & 0xFF); 
		result[3] = (byte)(i & 0xFF);
		return result;
	}


}
