package nl.topicus.onderwijs.wicket.i18n.plugin.model;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class WicketMessageKeyTree
{
	private boolean terminus = false;

	private Map<String, WicketMessageKeyTree> branches;

	public WicketMessageKeyTree()
	{
		this.branches = new TreeMap<>();
	}

	public void add(String key)
	{
		String[] parts = key.split("\\.", 2);

		String prefix = parts[0];
		String rest = parts.length == 2 ? parts[1] : null;

		if (!branches.containsKey(prefix))
		{
			branches.put(prefix, new WicketMessageKeyTree());
		}

		if (rest != null)
		{
			branches.get(prefix).add(rest);
		}
		else
		{
			branches.get(prefix).setTerminus(true);
		}

	}

	public boolean isTerminus()
	{
		return this.terminus;
	}

	public void setTerminus(boolean terminus)
	{
		this.terminus = terminus;
	}

	public WicketMessageKeyTree getBranch(String name)
	{
		return branches.get(name);
	}

	public Set<String> getBranchNames()
	{
		return branches.keySet();
	}

	public Set<String> getTerminals()
	{
		return branches.keySet().stream().filter(k -> branches.get(k).isTerminus())
			.collect(Collectors.toSet());
	}

	public boolean hasChildren()
	{
		return !branches.isEmpty();
	}
}
