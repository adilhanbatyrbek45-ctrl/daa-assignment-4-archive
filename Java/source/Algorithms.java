import java.util.*;

public final class Algorithms {
    static String str(Map<String,Object> x, String k) { return (String)x.get(k); }
    static int num(Map<String,Object> x, String k) { return ((Number)x.get(k)).intValue(); }
    static long lng(Object x) { return ((Number)x).longValue(); }
    static List<?> list(Map<String,Object> x, String k) { return (List<?>)x.get(k); }
    static Map<String,Object> out(Object... a) {
        Map<String,Object> m = new LinkedHashMap<>();
        for (int i=0;i<a.length;i+=2) m.put((String)a[i],a[i+1]);
        return m;
    }

    static Map<String,Object> lcs(Map<String,Object> input) {
        String a=str(input,"a"), b=str(input,"b");
        int n=a.length(), m=b.length();
        int[][] dp=new int[n+1][m+1];
        for(int i=1;i<=n;i++) for(int j=1;j<=m;j++)
            dp[i][j]=a.charAt(i-1)==b.charAt(j-1)?dp[i-1][j-1]+1:Math.max(dp[i-1][j],dp[i][j-1]);
        StringBuilder s=new StringBuilder();
        int i=n,j=m;
        while(i>0&&j>0) {
            if(a.charAt(i-1)==b.charAt(j-1)){s.append(a.charAt(i-1));i--;j--;}
            else if(dp[i-1][j]>=dp[i][j-1]) i--;
            else j--;
        }
        return out("length",dp[n][m],"sequence",s.reverse().toString());
    }

    static Map<String,Object> merge(Map<String,Object> input) {
        List<?> sizes=list(input,"sizes");
        int n=sizes.size();
        if(n<=1)return out("cost",0L,"plan",new ArrayList<>());
        long[] pref=new long[n+1];
        for(int i=0;i<n;i++)pref[i+1]=pref[i]+lng(sizes.get(i));
        long[][] dp=new long[n][n];
        int[][] split=new int[n][n];
        for(int len=2;len<=n;len++) for(int l=0;l+len<=n;l++) {
            int r=l+len-1;
            dp[l][r]=Long.MAX_VALUE/4;
            for(int k=l;k<r;k++) {
                long v=dp[l][k]+dp[k+1][r]+pref[r+1]-pref[l];
                if(v<dp[l][r]){dp[l][r]=v;split[l][r]=k;}
            }
        }
        List<List<Integer>> plan=new ArrayList<>();
        mergePlan(0,n-1,split,plan);
        return out("cost",dp[0][n-1],"plan",plan);
    }
    static void mergePlan(int l,int r,int[][] split,List<List<Integer>> plan) {
        if(l>=r)return;
        int k=split[l][r];
        mergePlan(l,k,split,plan);
        mergePlan(k+1,r,split,plan);
        plan.add(Arrays.asList(l,k,r));
    }

    static Map<String,Object> tree(Map<String,Object> input) {
        int n=num(input,"n");
        List<?> w=list(input,"weights"), edges=list(input,"edges");
        if(n==0)return out("weight",0L,"selected",new ArrayList<>());
        List<Integer>[] g=new ArrayList[n];
        for(int i=0;i<n;i++)g[i]=new ArrayList<>();
        for(Object eo:edges) {
            List<?> e=(List<?>)eo;
            int u=((Number)e.get(0)).intValue(),v=((Number)e.get(1)).intValue();
            g[u].add(v);g[v].add(u);
        }
        int[] parent=new int[n], order=new int[n];
        Arrays.fill(parent,-2);
        int count=0;
        int[] stack=new int[n];int top=0;stack[top++]=0;parent[0]=-1;
        while(top>0) {
            int u=stack[--top];order[count++]=u;
            for(int v:g[u])if(parent[v]==-2){parent[v]=u;stack[top++]=v;}
        }
        long[] take=new long[n],skip=new long[n];
        for(int oi=n-1;oi>=0;oi--) {
            int u=order[oi];
            take[u]=lng(w.get(u));skip[u]=0;
            for(int v:g[u])if(parent[v]==u) {
                take[u]+=skip[v];
                skip[u]+=Math.max(take[v],skip[v]);
            }
            take[u]=Math.max(take[u],Long.MIN_VALUE/4);
        }
        long best=Math.max(take[0],skip[0]);
        List<Integer> selected=new ArrayList<>();
        int[] nodes=new int[n];boolean[] canTake=new boolean[n];int sp=0;
        nodes[sp]=0;canTake[sp++]=true;
        while(sp>0) {
            int u=nodes[--sp];boolean allowed=canTake[sp];
            boolean choose=allowed&&take[u]>skip[u];
            if(choose)selected.add(u);
            for(int v:g[u])if(parent[v]==u){nodes[sp]=v;canTake[sp++]=!choose;}
        }
        return out("weight",best,"selected",selected);
    }

    static Map<String,Object> naive(Map<String,Object> input) {
        return out("positions",searchNaive(str(input,"text"),str(input,"pattern")));
    }
    static List<Integer> searchNaive(String text,String pattern) {
        int n=text.length(),m=pattern.length();List<Integer> ans=new ArrayList<>();
        if(m==0){for(int i=0;i<=n;i++)ans.add(i);return ans;}
        for(int i=0;i+m<=n;i++) {
            int j=0;while(j<m&&text.charAt(i+j)==pattern.charAt(j))j++;
            if(j==m)ans.add(i);
        }
        return ans;
    }
    static int[] prefix(String p) {
        int[] pi=new int[p.length()];
        for(int i=1,j=0;i<p.length();i++) {
            while(j>0&&p.charAt(i)!=p.charAt(j))j=pi[j-1];
            if(p.charAt(i)==p.charAt(j))j++;
            pi[i]=j;
        }
        return pi;
    }
    static List<Integer> searchKmp(String text,String pattern) {
        int n=text.length(),m=pattern.length();List<Integer> ans=new ArrayList<>();
        if(m==0){for(int i=0;i<=n;i++)ans.add(i);return ans;}
        if(m>n)return ans;
        int[] pi=prefix(pattern);
        for(int i=0,j=0;i<n;i++) {
            while(j>0&&text.charAt(i)!=pattern.charAt(j))j=pi[j-1];
            if(text.charAt(i)==pattern.charAt(j))j++;
            if(j==m){ans.add(i-m+1);j=pi[j-1];}
        }
        return ans;
    }
    static Map<String,Object> kmp(Map<String,Object> input) {
        return out("positions",searchKmp(str(input,"text"),str(input,"pattern")));
    }

    static Map<String,Object> rk(Map<String,Object> input) {
        String text=str(input,"text"),pattern=str(input,"pattern");
        int n=text.length(),m=pattern.length();List<Integer> ans=new ArrayList<>();
        if(m==0){for(int i=0;i<=n;i++)ans.add(i);return out("positions",ans);}
        if(m>n)return out("positions",ans);
        long mod=101,base=31,ph=0,wh=0,power=1;
        for(int i=0;i<m;i++) {
            ph=(ph*base+pattern.charAt(i))%mod;
            wh=(wh*base+text.charAt(i))%mod;
            if(i<m-1)power=(power*base)%mod;
        }
        for(int i=0;i<=n-m;i++) {
            if(ph==wh) {
                int j=0;while(j<m&&text.charAt(i+j)==pattern.charAt(j))j++;
                if(j==m)ans.add(i);
            }
            if(i<n-m) {
                wh=(wh-(text.charAt(i)*power)%mod+mod)%mod;
                wh=(wh*base+text.charAt(i+m))%mod;
            }
        }
        return out("positions",ans);
    }

    static Map<String,Object> suffix(Map<String,Object> input) {
        String s=str(input,"text");int n=s.length();
        if(n==0)return out("sa",new int[0],"lcp",new int[0],"repeated","");
        int[] sa=new int[n],rank=new int[n],next=new int[n];
        for(int i=0;i<n;i++){sa[i]=i;rank[i]=s.charAt(i);}
        for(int k=1;k<n;k*=2) {
            final int step=k;final int[] rr=rank;
            Integer[] boxed=new Integer[n];for(int i=0;i<n;i++)boxed[i]=sa[i];
            Arrays.sort(boxed,(x,y)->{
                if(rr[x]!=rr[y])return Integer.compare(rr[x],rr[y]);
                int a=x+step<n?rr[x+step]:-1,b=y+step<n?rr[y+step]:-1;
                return Integer.compare(a,b);
            });
            for(int i=0;i<n;i++)sa[i]=boxed[i];
            next[sa[0]]=0;
            for(int i=1;i<n;i++) {
                int a=sa[i-1],b=sa[i];
                boolean diff=rank[a]!=rank[b]||(a+k<n?rank[a+k]:-1)!=(b+k<n?rank[b+k]:-1);
                next[b]=next[a]+(diff?1:0);
            }
            int[] tmp=rank;rank=next;next=tmp;
            if(rank[sa[n-1]]==n-1)break;
            if(k>n/2)break;
        }
        int[] pos=new int[n],lcp=new int[n];
        for(int i=0;i<n;i++)pos[sa[i]]=i;
        int h=0,best=0,bestStart=0;
        for(int i=0;i<n;i++) {
            int r=pos[i];
            if(r==0)continue;
            int j=sa[r-1];
            while(i+h<n&&j+h<n&&s.charAt(i+h)==s.charAt(j+h))h++;
            lcp[r]=h;
            if(h>best){best=h;bestStart=i;}
            if(h>0)h--;
        }
        return out("sa",sa,"lcp",lcp,"repeated",s.substring(bestStart,bestStart+best));
    }

    static final int ALPH=95;
    static int charIndex(char c){return c-32;}
    static class Node {
        int[] next=new int[ALPH];
        int fail=0;
        Node(){Arrays.fill(next,-1);}
    }
    static Map<String,Object> aho(Map<String,Object> input) {
        List<?> patterns=list(input,"patterns"),texts=list(input,"texts");
        List<Node> nodes=new ArrayList<>();nodes.add(new Node());
        int[] terminal=new int[patterns.size()];
        for(int pi=0;pi<patterns.size();pi++) {
            String p=(String)patterns.get(pi);int state=0;
            for(int i=0;i<p.length();i++) {
                int c=charIndex(p.charAt(i));
                if(nodes.get(state).next[c]==-1) {
                    nodes.get(state).next[c]=nodes.size();nodes.add(new Node());
                }
                state=nodes.get(state).next[c];
            }
            terminal[pi]=state;
        }
        int[] bfs=new int[nodes.size()];int head=0,tail=0;
        for(int c=0;c<ALPH;c++) {
            int v=nodes.get(0).next[c];
            if(v==-1)nodes.get(0).next[c]=0;
            else {nodes.get(v).fail=0;bfs[tail++]=v;}
        }
        while(head<tail) {
            int u=bfs[head++];
            for(int c=0;c<ALPH;c++) {
                int v=nodes.get(u).next[c];
                if(v==-1)nodes.get(u).next[c]=nodes.get(nodes.get(u).fail).next[c];
                else {
                    nodes.get(v).fail=nodes.get(nodes.get(u).fail).next[c];
                    bfs[tail++]=v;
                }
            }
        }
        List<List<Long>> counts=new ArrayList<>();
        for(Object to:texts) {
            String text=(String)to;
            long[] visits=new long[nodes.size()];int state=0;
            for(int i=0;i<text.length();i++) {
                state=nodes.get(state).next[charIndex(text.charAt(i))];
                visits[state]++;
            }
            for(int i=tail-1;i>=0;i--) {
                int v=bfs[i];visits[nodes.get(v).fail]+=visits[v];
            }
            List<Long> row=new ArrayList<>();
            for(int v:terminal)row.add(visits[v]);
            counts.add(row);
        }
        return out("counts",counts);
    }
}
